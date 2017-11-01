package com.qihoo.multids.mapper;

import com.qihoo.multids.IMapper;
import com.qihoo.multids.annotation.DynamicDataSource;
import com.qihoo.multids.vo.TestVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
@Mapper
public interface TestMapper extends IMapper {
    @DynamicDataSource("group1")
    List<TestVO> queryInfo();
}
