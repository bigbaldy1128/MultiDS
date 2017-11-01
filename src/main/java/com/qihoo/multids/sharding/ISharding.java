package com.qihoo.multids.sharding;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
public interface ISharding {

    String route(Long key);

    void initNodes(List<String> nodes);
}
