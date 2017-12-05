package com.qihoo.multids;

import com.qihoo.multids.annotation.DynamicDataSource;
import com.qihoo.multids.annotation.ShardingKey;
import com.qihoo.multids.dbconfig.DataSourceConfig;
import com.qihoo.multids.dbconfig.DataSourceContextHolder;
import com.qihoo.multids.sharding.ISharding;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
@Aspect
@Component
@Order(100)
public class MultiDSIntercepter {
    private final DataSourceConfig dataSourceConfig;

    @Autowired
    public MultiDSIntercepter(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

//    @Before("within(@org.springframework.web.bind.annotation.RestController *) ||" +
//            "within(@org.springframework.stereotype.Controller *)")
//    public void doSetShardingKey(JoinPoint joinPoint) throws InstantiationException, IllegalAccessException, ShardingException {
//        Long key = shardingKey.getKey(joinPoint.getArgs());
//        DataSourceContextHolder.setDB(dataSourceConfig.getDefaultId());
//        if (key != null) {
//            UserContextHolder.setUserId(key);
//        }
//    }

    @Before("execution(* com.qihoo.multids.IMapper+.*(..))")
    public void doSwitchDS(JoinPoint joinPoint) {
        DataSourceContextHolder.setDB(dataSourceConfig.getDefaultId());
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DynamicDataSource dynamicDataSource = method.getAnnotation(DynamicDataSource.class);
        if (dynamicDataSource != null) {
            int key = -1;
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                if (Arrays.stream(parameterAnnotations[i]).anyMatch(p -> p instanceof ShardingKey)) {
                    key = args[i].hashCode();
                    break;
                } else {
                    Parameter parameter = parameters[i];
                    Class<?> clazz = parameter.getType();
                    for (Field field : clazz.getDeclaredFields()) {
                        if (Arrays.stream(field.getAnnotations()).anyMatch(p->p instanceof ShardingKey)) {
                            field.setAccessible(true);
                            Object fieldValue = null;
                            try {
                                fieldValue = field.get(args[i]);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            if (fieldValue != null) {
                                key = fieldValue.hashCode();
                            }
                            break;
                        }
                    }
                }
            }
            if (key != -1) {
                ISharding sharding = dataSourceConfig.getShardingMap().get(dynamicDataSource.value());
                DataSourceContextHolder.setDB(sharding.route(key));
            }
        }
    }
}
