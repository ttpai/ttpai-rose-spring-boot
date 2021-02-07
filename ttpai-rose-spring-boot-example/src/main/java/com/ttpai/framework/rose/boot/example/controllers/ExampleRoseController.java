package com.ttpai.framework.rose.boot.example.controllers;

import net.paoding.rose.web.annotation.Param;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Path("/rose")
public class ExampleRoseController {

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

}
