# MultiDS
[![](https://jitpack.io/v/bigbaldy1128/MultiDS.svg)](https://jitpack.io/#bigbaldy1128/MultiDS)   
MultiDS是一个简化多数据源操作的类库，主要特点如下：
* 零代码侵入性,通过简单的配置即可实现功能
* 提供两种分片算法（一致性哈希、取模），默认采用一致性Hash算法进行数据分片，并可自定义分片算法
* 整合了阿里的druid
## 如何使用
1. 安装
* 外网 Please refer https://jitpack.io/#bigbaldy1128/MultiDS
* 内网
```xml
<dependency>
    <groupId>com.qihoo</groupId>
    <artifactId>multiple-data-source</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 配置Mapper
```java
@Mapper
public interface YourMapper extends IMapper { //需要继承IMapper接口
    @DynamicDataSource("group1") //配置动态数据源对应的组id，若不配置则使用默认数据源
    List<TestVO> query(@ShardingKey int key); //配置分片key（@ShardingKey也可以标记在类的成员变量上）
}
```
3. 配置多数据源
```yml
multids:
  shardingWay: consistentHash #配置分片算法，可选consistentHash或mod，若不配置默认使用一致性哈希算法
  data-sources:
    - id: ds1 #数据源id，必须配置
      url: jdbc:mysql://localhost:3306/db1
      username: root
      password: ct123!@#
      driver-class-name: com.mysql.jdbc.Driver
      isDefault: true #设置默认数据源，若不设置则默认数据源就是第一个数据源
      groupId: group1 #分组id
    - id: ds2
      druid: #使用druid
        url: jdbc:mysql://localhost:3306/db2
        username: root
        password: ct123!@#
        driver-class-name: com.mysql.jdbc.Driver
        groupId: group1
```
4. 自定义数据源分片算法
```java
public class ModSharding implements ISharding { //需要继承ISharding接口
    @Override
    public String route(Object key) { //根据传入的id(例如用户id)获取目的数据源id
        throw new NotImplementedException();
    }

    @Override
    public void initNodes(List<String> nodes) { //设置数据源节点
        throw new NotImplementedException();
    }
}
```