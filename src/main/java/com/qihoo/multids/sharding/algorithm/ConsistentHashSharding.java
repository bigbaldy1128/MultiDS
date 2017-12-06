package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;
import lombok.val;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
public class ConsistentHashSharding<K> implements ISharding<K> {
    private ConsistentHash<String, K> consistentHash;

    @Override
    public String route(K key) {
        return consistentHash.get(key);
    }

    @Override
    public void initNodes(List<String> nodes) {
        consistentHash = new ConsistentHash<>(nodes);
    }

    @Override
    public Map<String, List<K>> getMigrationData(List<K> keys, List<String> oldNodes, List<String> newNodes) {
        Map<String, List<K>> ret = new HashMap<>();
        val consistentHashOld = new ConsistentHash<String, K>(oldNodes);
        List<String> consistentHashList = new ArrayList<>();
        Map<Object, String> oldMap = new HashMap<>();
        for (K key : keys) {
            String node = consistentHashOld.get(key);
            oldMap.put(key, node);
        }
        consistentHashList.addAll(oldNodes);
        consistentHashList.addAll(newNodes);
        val consistentHashNew = new ConsistentHash<String, K>(consistentHashList);
        for (K key : keys) {
            String newNode = consistentHashNew.get(key);
            String oldNode = oldMap.get(key);
            if (!newNode.equals(oldNode)) {
                String node = oldNode + "🍄" + newNode;
                List<K> migrationData;
                if (ret.containsKey(node)) {
                    migrationData = ret.get(node);
                } else {
                    migrationData = new ArrayList<>();
                    ret.put(node, migrationData);
                }
                migrationData.add(key);
            }
        }
        return ret;
    }


}
