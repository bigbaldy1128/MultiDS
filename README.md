# MultiDS简介
[![](https://jitpack.io/v/bigbaldy1128/MultiDS.svg)](https://jitpack.io/#bigbaldy1128/MultiDS)   
MultiDS是一个简化多数据源操作的类库，主要特点如下：
* 零代码侵入性,通过简单的配置即可实现功能
* 提供两种分片算法（一致性哈希、取模），默认采用一致性Hash算法进行数据分片，并可自定义分片算法
* 整合了阿里的druid
# 如何使用
## 安装
* 外网 Please refer https://jitpack.io/#bigbaldy1128/MultiDS
* 内网
```xml
<dependency>
    <groupId>com.qihoo</groupId>
    <artifactId>multiple-data-source</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 配置Mapper
```java
@Mapper
public interface YourMapper extends IMapper { //需要继承IMapper接口
    @DynamicDataSource("group1") //配置动态数据源对应的组id，若不配置则使用默认数据源
    List<TestVO> query(@ShardingKey int key); //配置分片key（@ShardingKey也可以标记在类的成员变量上）
}
```
## 配置多数据源
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
## 自定义数据源分片算法
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

    /**
     * 获取要迁移的数据信息
     * @param keys 分片键
     * @param oldNodes 旧节点
     * @param newNodes 新节点
     * @return key：旧节点+🍄+新节点，value：分片键
     */
    @Override
    public Map<String,List<Object>> getMigrationData(List<Object> keys, List<String> oldNodes, List<String> newNodes){
        throw new NotImplementedException();
    }
}
```
## 重新平衡数据
1. 实现数据迁移方法
```java
@Migrate(from="ds1",to="ds2") //配置迁移方向，例如从数据源ds1迁移到ds2
public void migrationData(List<Object> keys){//keys是要迁移的数据分片键，注意函数必须如此定义
    //实现数据迁移
}
```
2. 调用DBUtils.rebalance方法
```java
/**
* 平衡数据
*
* @param sharding 分片方法
* @param keys     分片键
* @param oldNodes 旧节点
* @param newNodes 新节点
* @throws Exception
*/
public static void rebalance(ISharding sharding, List<Object> keys, List<String> oldNodes, List<String> newNodes) throws Exception
```