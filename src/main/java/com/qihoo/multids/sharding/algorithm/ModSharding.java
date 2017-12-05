package com.qihoo.multids.sharding.algorithm;

import com.qihoo.multids.sharding.ISharding;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/31.
 */
public class ModSharding implements ISharding {
    private List<String> nodes;

    @Override
    public String route(int key) {
        return nodes.get(key % nodes.size());
    }

    @Override
    public void initNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
