package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;

import java.util.*;

/**
 * Created by wangjinzhao on 2017/10/31.
 */
public class ModSharding implements ISharding {
    private List<String> nodes;

    @Override
    public String route(Object key) {
        return nodes.get(key.hashCode() % nodes.size());
    }

    @Override
    public void initNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Map<String, List<Object>> getMigrationData(List<Object> keys, List<String> oldNodes, List<String> newNodes) {
        Map<String, List<Object>> ret = new HashMap<>();
        List<String> newList = new ArrayList<>();
        newList.addAll(oldNodes);
        newList.addAll(newNodes);
        for (Object key : keys) {
            int hashCode = key.hashCode();
            int newIndex = hashCode % oldNodes.size();
            int oldIndex = hashCode % newList.size();
            if (newIndex != oldIndex) {
                String node = oldIndex + "🍄" + newIndex;
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
