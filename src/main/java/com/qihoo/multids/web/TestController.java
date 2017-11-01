package com.qihoo.multids.web;

import com.qihoo.multids.mapper.TestMapper;
import com.qihoo.multids.vo.TestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wangjinzhao on 2017/10/30.
 */
@RestController
public class TestController {
    @Autowired
    private TestMapper testMapper;

    @GetMapping("/multids/test")
    public List<TestVO> test(){
        return testMapper.queryInfo();
    }
}
