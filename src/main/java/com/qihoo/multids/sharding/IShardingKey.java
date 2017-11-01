package com.qihoo.multids.sharding;

import com.qihoo.multids.exception.ShardingException;

/**
 * Created by wangjinzhao on 2017/10/30.
 */
public interface IShardingKey {
    Long getKey(Object... params) throws ShardingException;
}
