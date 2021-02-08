package com.ttpai.framework.rose.boot.example.controllers;

import lombok.extern.slf4j.Slf4j;
import net.paoding.rose.web.ControllerInterceptorAdapter;
import net.paoding.rose.web.Invocation;
import org.apache.commons.lang.StringUtils;

/**
 * 【拦截器】测试
 */

@Slf4j
public class ExampleRoseInterceptor extends ControllerInterceptorAdapter {

    private static final String JSON_TYPE = "@json:";

    /**
     * 拦截前操作，权限校验等
     */
    @Override
    public Object before(Invocation inv) throws Exception {
        String appid = inv.getParameter("appid");
        if (StringUtils.isEmpty(appid)) {
            return JSON_TYPE + "缺少 appid 参数";
        }

        return super.before(inv);
    }

    /**
     *
     */
    @Override
    protected Object after(Invocation inv, Object instruction) throws Exception {
        String result = (String) instruction;
        if (result.startsWith(JSON_TYPE)) {
            String jsonData = result.replaceFirst(JSON_TYPE, "");
            instruction = "@" + jsonData + "(json)";
        }
        return instruction;
    }

    /**
     *
     */
    @Override
    public void afterCompletion(Invocation inv, Throwable ex) throws Exception {
        log.warn("{} afterCompletion", inv.getMethod(), ex);
    }
}
