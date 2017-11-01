package com.qihoo.multids.dbconfig;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiDataSourceProperties extends DataSourceProperties {
    private String id;
    private String groupId = "default";
    private String isDefault="";
    private DruidDataSource druid;
}
