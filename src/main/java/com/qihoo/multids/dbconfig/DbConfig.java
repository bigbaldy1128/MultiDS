package com.qihoo.multids.dbconfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by wangjinzhao on 2017/7/4.
 */
@Configuration
@MapperScan(basePackages = {"mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DbConfig {

    private final DataSourceConfiguration dataSourceConfiguration;

    @Autowired
    public DbConfig(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dynamicDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        if (dataSourceConfiguration.getMapperLocations() != null) {
            Resource[] mapperLocations = Arrays.stream(dataSourceConfiguration.getMapperLocations()).flatMap(p -> {
                try {
                    return Arrays.stream(resolver.getResources(p));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList()).toArray(new Resource[0]);
            factoryBean.setMapperLocations(mapperLocations);
        } else {
            factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*"));
        }
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
