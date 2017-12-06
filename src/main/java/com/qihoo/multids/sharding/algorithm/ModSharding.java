package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;

import java.util.*;

/**
 * Created by wangjinzhao on 2017/10/31.
 */
public class ModSharding<K> implements ISharding<K> {
    private List<String> nodes;

    @Override
    public String route(K key) {
        return nodes.get(key.hashCode() % nodes.size());
    }

    @Override
    public void initNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Map<String, List<K>> getMigrationData(List<K> keys, List<String> oldNodes, List<String> newNodes) {
        Map<String, List<K>> ret = new HashMap<>();
        List<String> newList = new ArrayList<>();
        newList.addAll(oldNodes);
        newList.addAll(newNodes);
        for (K key : keys) {
            int hashCode = key.hashCode();
            String oldNode = oldNodes.get(hashCode % oldNodes.size());
            String newNode = newList.get(hashCode % newList.size());
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
