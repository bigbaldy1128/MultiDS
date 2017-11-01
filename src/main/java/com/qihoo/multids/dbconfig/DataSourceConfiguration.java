package com.qihoo.multids.dbconfig;

import com.qihoo.multids.sharding.ShardingWayEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/30.
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "multids")
public class DataSourceConfiguration {
    private List<MultiDataSourceProperties> dataSources;
    private String[] mapperLocations;
    private String shardingWay = ShardingWayEnum.CONSISTENT_HASH.getValue();
}
