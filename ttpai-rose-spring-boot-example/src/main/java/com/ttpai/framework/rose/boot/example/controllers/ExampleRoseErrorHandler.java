package com.ttpai.framework.rose.boot.example.controllers;

import lombok.extern.slf4j.Slf4j;
import net.paoding.rose.web.ControllerErrorHandler;
import net.paoding.rose.web.Invocation;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 【异常处理器】测试
 */
@Slf4j
public class ExampleRoseErrorHandler implements ControllerErrorHandler {

    private static final String STATUS_CODE_ATTR = "javax.servlet.error.status_code";

    private static final String APPACHE_TOMCAT = "Apache Tomcat";

    private static final String EXCEPTION_ATTR = "javax.servlet.error.exception";

    private static final String EXCEPTION_TYPE_ATTR = "javax.servlet.error.exception_type";

    @Override
    public Object onError(Invocation inv, Throwable ex) throws Throwable {
        final HttpServletRequest request = inv.getRequest();

        // 处理 tomcat 容器自带异常报告
        String serverInfo = inv.getApplicationContext().getServletContext().getServerInfo();
        Object state = request.getAttribute(STATUS_CODE_ATTR);
        if (StringUtils.containsIgnoreCase(serverInfo, APPACHE_TOMCAT)
                && null != state && "200".equals(state.toString())) {
            request.removeAttribute(EXCEPTION_TYPE_ATTR);
            request.removeAttribute(EXCEPTION_ATTR);
        }

        log.error("【异常处理器】", ex);

        return "@json:【异常处理器】" + ex.getMessage();
    }
}
