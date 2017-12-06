package com.qihoo.multids.tools;

import com.qihoo.multids.dbconfig.DataSourceContextHolder;
import com.qihoo.multids.sharding.ISharding;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DBUtils {

    /**
     * 平衡数据
     *
     * @param sharding    分片算法
     * @param migrateData 数据迁移的具体实现
     * @param oldNodes    旧节点
     * @param newNodes    新节点
     * @param nThreads    线程数
     * @throws Exception
     */
    public static <T, K> void rebalance(
            ISharding<K> sharding,
            IMigrateData<T, K> migrateData,
            List<String> oldNodes,
            List<String> newNodes,
            int nThreads) throws Exception {
        List<K> keyList = new ArrayList<>();
        for (String node : oldNodes) {
            DataSourceContextHolder.setDB(node);
            keyList.addAll(migrateData.getShardingKeys());
        }
        val migrationDataMap = sharding.getMigrationData(keyList, oldNodes, newNodes);
        List<String> newList = new ArrayList<>();
        newList.addAll(oldNodes);
        newList.addAll(newNodes);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<?>> futures = new ArrayList<>();
        for (String oldNode : oldNodes) {
            for (String newNode : newList) {
                String node = oldNode + "🍄" + newNode;
                val keys = migrationDataMap.get(node);
                if (keys != null && keys.size() > 0) {
                    futures.add(executorService.submit(() -> {
                        for (K key : keys) {
                            DataSourceContextHolder.setDB(oldNode);
                            val data = migrateData.query(key);
                            DataSourceContextHolder.setDB(newNode);
                            migrateData.insert(data, key);
                            DataSourceContextHolder.setDB(oldNode);
                            migrateData.delete(key);
                        }
                    }));
                }
            }
        }
        futures.forEach(p -> {
            try {
                p.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }
}
