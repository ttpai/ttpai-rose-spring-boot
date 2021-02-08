package com.ttpai.framework.rose.boot.example.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
