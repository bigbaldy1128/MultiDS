package com.qihoo.multids.sharding.shardingkey;

import com.qihoo.multids.exception.ShardingException;
import com.qihoo.multids.sharding.IShardingKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * Created by wangjinzhao on 2017/10/30.
 */
@Component
@ConditionalOnMissingBean(IShardingKey.class)
public class ShardingKeyByHeaderUserID implements IShardingKey {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Long getKey(Object... params) throws ShardingException {
        //return (long) new Random().nextInt(2); //测试用
        String userIdStr = httpServletRequest.getHeader("user-id");
        if (userIdStr == null) {
            throw new ShardingException("请求头中的user-id为空");
        }
        return Long.parseLong(userIdStr);
    }
}
