package com.ttpai.framework.rose.boot.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ResponseBody
@ControllerAdvice
public class MvcExceptionAdvice {

    /**
     * 全局异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception ex) {
        log.error("error:{}", buildRequestInfo(request));
        return "error:" + ex.getMessage();
    }

    private StringBuilder buildRequestInfo(HttpServletRequest request) {
        StringBuilder reqBuilder = new StringBuilder(System.lineSeparator());
        if (null == request) {
            return reqBuilder;
        }

        String lineEnd = System.lineSeparator();

        reqBuilder.append("Method:[").append(request.getMethod()).append("]").append(lineEnd);

        reqBuilder.append("RequestURL:[").append(request.getRequestURL()).append("]").append(lineEnd);

        return reqBuilder;
    }

}
