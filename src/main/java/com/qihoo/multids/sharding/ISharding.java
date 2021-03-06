package com.qihoo.multids.sharding;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
public interface ISharding<K> {

    String route(K key);

    void initNodes(List<String> nodes);

    /**
     * 获取要迁移的数据信息
     *
     * @param keys     分片键
     * @param oldNodes 旧节点
     * @param newNodes 新节点
     * @return key：旧节点+新节点，value：分片键
     */
    Map<String, List<K>> getMigrationData(List<K> keys, List<String> oldNodes, List<String> newNodes);
}
