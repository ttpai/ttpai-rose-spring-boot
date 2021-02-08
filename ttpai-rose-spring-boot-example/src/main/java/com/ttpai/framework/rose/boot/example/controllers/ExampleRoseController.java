package com.ttpai.framework.rose.boot.example.controllers;

import com.ttpai.framework.rose.boot.example.dao.UserDAO;
import com.ttpai.framework.rose.boot.example.dao.UserMapper;
import net.paoding.rose.web.annotation.Param;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Path("/rose")
public class ExampleRoseController {

    @Resource
    private UserDAO userDAO;

    @Resource
    private UserMapper userMapper;

    @Get("/hello")
    public String hello() {
        return "@hello rose";
    }

    @Get("/exception")
    public String exception(@Required @Param("m") String message) {
        if (StringUtils.isNotBlank(message)) {
            throw new IllegalArgumentException(message);
        }
        return "@【Return】缺少 m 参数";
    }

    @Get("/jsp")
    public String jsp(HttpServletRequest request) {
        request.setAttribute("message", System.currentTimeMillis());
        return "index";
    }

    @Get("/jade")
    public String jade() {
        return "@jade:" + userDAO.count();
    }

    @Get("/mybatis")
    public String mybatis() {

        System.out.println(userMapper.selectAll());

        return "@mybatis:" + userMapper.count();
    }

}
