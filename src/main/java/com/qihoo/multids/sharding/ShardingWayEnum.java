package com.qihoo.multids.sharding;

/**
 * Created by wangjinzhao on 2017/10/31.
 */
public enum ShardingWayEnum {
    CONSISTENT_HASH("consistentHash"),
    MOD("mod");

    private String value;

    ShardingWayEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
