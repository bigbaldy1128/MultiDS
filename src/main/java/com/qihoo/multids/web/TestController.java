package com.qihoo.multids.web;

import com.qihoo.multids.mapper.TestMapper;
import com.qihoo.multids.vo.TestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/30.
 */
@RestController
public class TestController {
    @Autowired
    private TestMapper testMapper;

    @GetMapping("/multids/test/{id}")
    public List<TestVO> test(@PathVariable int id){
        return testMapper.queryInfo(id);
    }

    @GetMapping("/multids/test2/{id}")
    public List<TestVO> test2(@PathVariable int id){
        TestVO testVO=new TestVO();
        testVO.setKey(id);
        return testMapper.queryInfo2(testVO);
    }
}
