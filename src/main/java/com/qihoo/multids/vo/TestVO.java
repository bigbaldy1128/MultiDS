package com.qihoo.multids.vo;

import com.qihoo.multids.annotation.ShardingKey;
import lombok.Data;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
@Data
public class TestVO {
    @ShardingKey
    private int key;

    private String name;
}
