package com.qihoo.multids.tools;

import com.qihoo.multids.annotation.Migrate;
import com.qihoo.multids.exception.ShardingException;
import com.qihoo.multids.sharding.ISharding;
import lombok.val;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;

public class DBUtils {
    /**
     * 平衡数据
     *
     * @param sharding
     * @param keys     分片键
     * @param oldNodes 旧节点
     * @param newNodes 新节点
     * @throws Exception
     */
    public static void rebalance(ISharding sharding, List<Object> keys, List<String> oldNodes, List<String> newNodes) throws Exception {
        val migrationDataMap = sharding.getMigrationData(keys, oldNodes, newNodes);
        val methods = new Reflections("com.qihoo").getMethodsAnnotatedWith(Migrate.class);
        Object object = null;
        val methodOpt = methods.stream().findFirst();
        if (methodOpt.isPresent()) {
            object = methodOpt.get().getDeclaringClass().newInstance();
        }
        if (object == null) {
            throw new ShardingException("初始化数据迁移类失败");
        }
        for (val method : methods) {
            val annotationOptional = Arrays.stream(method.getAnnotations()).filter(p -> p instanceof Migrate).findFirst();
            if (annotationOptional.isPresent()) {
                Migrate migrate = (Migrate) annotationOptional.get();
                String from = migrate.from();
                String to = migrate.to();
                String fromTo = from + "🍄" + to;
                if (migrationDataMap.containsKey(fromTo)) {
                    List<Object> _keys = migrationDataMap.get(fromTo);
                    method.invoke(object, _keys);
                }
            }
        }
    }
}
