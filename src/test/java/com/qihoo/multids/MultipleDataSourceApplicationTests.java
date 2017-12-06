package com.qihoo.multids;

import com.qihoo.multids.mapper.TestMapper;
import com.qihoo.multids.sharding.algorithm.ModSharding;
import com.qihoo.multids.tools.DBUtils;
import com.qihoo.multids.tools.IMigrateData;
import com.qihoo.multids.vo.TestVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MultipleDataSourceApplication.class)
public class MultipleDataSourceApplicationTests {

    @Autowired
    private TestMapper testMapper;

    @Test
    public void test1() throws Exception {
        DBUtils.setnThreads(2);
        DBUtils.rebalance(new ModSharding<>(), new IMigrateData<TestVO,Integer>() {
            @Override
            public List<Integer> getShardingKeys() {
                return Arrays.asList(1, 2, 3);
            }

            @Override
            public List<TestVO> query(Integer key) {
                return testMapper.queryInfo(key);
            }

            @Override
            public void delete(Integer key) {

            }
            @Override
            public void insert(List<TestVO> data,Integer key) {
                testMapper.inserInfo(data, key);
            }
        }, Arrays.asList("ds1", "ds2"), Collections.singletonList("ds3"));
        System.out.println(123);
    }

    @Test
    public void test2(){
        Assert.assertEquals(testMapper.queryInfo2(0).stream().findFirst().get().getName(),"db1");
        Assert.assertEquals(testMapper.queryInfo2(1).stream().findFirst().get().getName(),"db2");
        Assert.assertEquals(testMapper.queryInfo2(2).stream().findFirst().get().getName(),"db3");
    }

}
