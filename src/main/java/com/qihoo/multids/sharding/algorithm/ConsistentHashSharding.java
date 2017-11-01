package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
public class ConsistentHashSharding implements ISharding {
    private ConsistentHash<String> consistentHash;

    @Override
    public String route(Long key) {
        return consistentHash.get(key);
    }

    @Override
    public void initNodes(List<String> nodes) {
        consistentHash = new ConsistentHash<>(nodes);
    }
}
