package com.ttpai.framework.rose.boot.example.controllers;

import net.paoding.rose.web.annotation.rest.Get;
import net.paoding.rose.web.annotation.rest.Post;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mvc")
public class MvcController {

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello mvc";
    }

    @ResponseBody
    @GetMapping("/exception")
    public String exception(String message) {
        if (StringUtils.isNotBlank(message)) {
            throw new IllegalArgumentException(message);
        }
        return "@【Return】缺少 message 参数";
    }

    @GetMapping("/jsp")
    public String jsp(HttpServletRequest request) {
        request.setAttribute("framework", "mvc");
        request.setAttribute("message", System.currentTimeMillis());
        return "index";
    }

}
