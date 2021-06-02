package com.ttpai.framework.rose.boot.example.runner;

import com.ttpai.framework.rose.boot.example.dao.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyBatisRunner implements CommandLineRunner {

    @Resource
    private UserMapper userMapper;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(userMapper.count());
    }
}
