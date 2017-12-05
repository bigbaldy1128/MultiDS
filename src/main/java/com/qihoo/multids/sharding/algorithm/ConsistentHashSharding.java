package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;
import lombok.val;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
public class ConsistentHashSharding implements ISharding {
    private ConsistentHash<String> consistentHash;

    @Override
    public String route(Object key) {
        return consistentHash.get(key);
    }

    @Override
    public void initNodes(List<String> nodes) {
        consistentHash = new ConsistentHash<>(nodes);
    }

    @Override
    public Map<String, List<Object>> getMigrationData(List<Object> keys, List<String> oldNodes, List<String> newNodes) {
        Map<String, List<Object>> ret = new HashMap<>();
        val consistentHashOld = new ConsistentHash<String>(oldNodes);
        List<String> consistentHashList = new ArrayList<>();
        Map<Object, String> oldMap = new HashMap<>();
        for (Object key : keys) {
            String node = consistentHashOld.get(key);
            oldMap.put(key, node);
        }
        consistentHashList.addAll(oldNodes);
        consistentHashList.addAll(newNodes);
        val consistentHashNew = new ConsistentHash<String>(consistentHashList);
        for (Object key : keys) {
            String newNode = consistentHashNew.get(key);
            String oldNode = oldMap.get(key);
            if (!newNode.equals(oldNode)) {
                String node = oldNode + "🍄" + newNode;
                List<Object> migrationData;
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
