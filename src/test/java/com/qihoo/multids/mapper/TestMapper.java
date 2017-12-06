package com.qihoo.multids.mapper;

import com.qihoo.multids.IMapper;
import com.qihoo.multids.annotation.DynamicDataSource;
import com.qihoo.multids.annotation.ShardingKey;
import com.qihoo.multids.vo.TestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestMapper extends IMapper {
    List<TestVO> queryInfo(@Param("id") int id);

    void inserInfo(@Param("testVO") List<TestVO> testVO,@Param("id") int id);

    @DynamicDataSource("group1")
    List<TestVO> queryInfo2(@ShardingKey @Param("id") int id);
}
