package com.qihoo.multids.dbconfig;

import com.qihoo.multids.exception.ShardingException;
import com.qihoo.multids.sharding.ISharding;
import com.qihoo.multids.sharding.ShardingWayEnum;
import com.qihoo.multids.sharding.algorithm.ConsistentHashSharding;
import com.qihoo.multids.sharding.algorithm.ModSharding;
import lombok.Data;
import lombok.val;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wangjinzhao on 2017/7/4.
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Data
public class DataSourceConfig {
    private final DataSourceConfiguration dataSourceConfiguration;

    private Map<String, ISharding> shardingMap;
    private String defaultId;

    @Autowired
    public DataSourceConfig(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean
    public DynamicDataSource dataSource() throws IllegalAccessException, InstantiationException, ShardingException {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dsMap = new HashMap<>();
        for (MultiDataSourceProperties dataSourceSetting : dataSourceConfiguration.getDataSources()) {
            DataSource dataSource;
            if (dataSourceSetting.getDruid() != null) {
                dataSource = dataSourceSetting.getDruid();
            } else {
                dataSource = DataSourceBuilder.create()
                        .url(dataSourceSetting.getUrl())
                        .username(dataSourceSetting.getUsername())
                        .password(dataSourceSetting.getPassword())
                        .driverClassName(dataSourceSetting.getDriverClassName())
                        .build();
            }
            if (dataSourceSetting.getIsDefault().equals("true")) {
                this.defaultId = dataSourceSetting.getId();
                dynamicDataSource.setDefaultTargetDataSource(dataSource);
            }
            dsMap.put(dataSourceSetting.getId(), dataSource);
        }
        if (dataSourceConfiguration.getDataSources().stream().noneMatch(p -> p.getIsDefault().equals("true"))) {
            dynamicDataSource.setDefaultTargetDataSource(dataSourceConfiguration.getDataSources().get(0));
        }
        dynamicDataSource.setTargetDataSources(dsMap);
        val dataSourceSettings = dataSourceConfiguration.getDataSources().stream().collect(Collectors.groupingBy(MultiDataSourceProperties::getGroupId));
        val refs = new Reflections("com.qihoo").getSubTypesOf(ISharding.class);
        shardingMap = new HashMap<>();
        Class<? extends ISharding> shardingClass;
        String shardingWay = dataSourceConfiguration.getShardingWay();
        if (shardingWay.equals(ShardingWayEnum.CONSISTENT_HASH.getValue())) {
            shardingClass = ConsistentHashSharding.class;
        } else if (shardingWay.equals(ShardingWayEnum.MOD.getValue())) {
            shardingClass = ModSharding.class;
        } else {
            val shardingClassOptional = refs.stream()
                    .filter(p ->
                            !p.getSimpleName().equals("ConsistentHashSharding") &&
                                    !p.getSimpleName().equals("ModSharding"))
                    .findFirst();
            if (!shardingClassOptional.isPresent()) {
                throw new ShardingException("没有配置分片算法或配置的算法名称有误");
            }
            shardingClass = shardingClassOptional.get();
        }

        for (val dataSourceSetting : dataSourceSettings.entrySet()) {
            val nodes = dataSourceSetting
                    .getValue()
                    .stream()
                    .map(MultiDataSourceProperties::getId)
                    .collect(Collectors.toList());
            ISharding sharding = shardingClass.newInstance();
            sharding.initNodes(nodes);
            shardingMap.put(dataSourceSetting.getKey(), sharding);
        }
        return dynamicDataSource;
    }
}
