package com.qihoo.multids;

import com.qihoo.multids.annotation.DynamicDataSource;
import com.qihoo.multids.dbconfig.DataSourceConfig;
import com.qihoo.multids.dbconfig.DataSourceContextHolder;
import com.qihoo.multids.exception.ShardingException;
import com.qihoo.multids.sharding.ISharding;
import com.qihoo.multids.sharding.IShardingKey;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
@Aspect
@Component
@Order(100)
public class MultiDSIntercepter {
    @Autowired
    private
    ApplicationContext applicationContext;
    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private IShardingKey shardingKey;

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Map<String, IShardingKey> shardingKeyMap = applicationContext.getBeansOfType(IShardingKey.class, false, false);
//        if (shardingKeyMap.size() == 1) {
//            shardingKey = shardingKeyMap.get("shardingKeyByHeaderUserID");
//        } else {
//            val shardingKeyOptional = shardingKeyMap.keySet().stream().filter(p -> !p.equals("shardingKeyByHeaderUserID")).findFirst();
//            if (!shardingKeyOptional.isPresent()) {
//                throw new ShardingException("没有定义获取分片key的方式");
//            }
//            shardingKey = shardingKeyMap.get(shardingKeyOptional.get());
//        }
//    }

    @Before("within(@org.springframework.web.bind.annotation.RestController *) ||" +
            "within(@org.springframework.stereotype.Controller *)")
    public void doSetShardingKey(JoinPoint joinPoint) throws InstantiationException, IllegalAccessException, ShardingException {
//        if (shardingKey == null) {
//            InitShardingKey();
//        }
        Long key = shardingKey.getKey(joinPoint.getArgs());
        DataSourceContextHolder.setDB(dataSourceConfig.getDefaultId());
        if (key != null) {
            UserContextHolder.setUserId(key);
        }
    }

    @Before("execution(* com.qihoo.multids.IMapper+.*(..))")
    public void doSwitchDS(JoinPoint joinPoint) {
        DynamicDataSource dynamicDataSource = ((MethodSignature) joinPoint
                .getSignature())
                .getMethod()
                .getAnnotation(DynamicDataSource.class);
        if (dynamicDataSource != null) {
            Long userId = UserContextHolder.getUserId();
            if (userId != null) {
                ISharding sharding = dataSourceConfig.getShardingMap().get(dynamicDataSource.value());
                DataSourceContextHolder.setDB(sharding.route(userId));
            }
        }
    }
}
