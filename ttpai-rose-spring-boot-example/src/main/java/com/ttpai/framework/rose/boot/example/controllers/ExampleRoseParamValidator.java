package com.ttpai.framework.rose.boot.example.controllers;

import net.paoding.rose.web.Invocation;
import net.paoding.rose.web.ParamValidator;
import net.paoding.rose.web.paramresolver.ParamMetaData;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

/**
 * 【校验器】测试
 */
public class ExampleRoseParamValidator implements ParamValidator {

    /**
     * 支持的注解
     */
    @Override
    public boolean supports(ParamMetaData metaData) {
        return null != metaData.getAnnotation(Required.class);
    }

    /**
     * 校验逻辑
     *
     * @param metaData 参数的原型
     * @param inv      是rose的基础调用
     * @param target   是这个参数的最后解析结果
     * @param errors   是这个参数解析时出来的错误
     */
    @Override
    public Object validate(ParamMetaData metaData, Invocation inv, Object target, Errors errors) {
        String param = metaData.getParamName();
        String value = inv.getParameter(param);

        if (StringUtils.isEmpty(value)) {
            return "@【校验器】参数不能为空";
        }
        if (null == target) {
            return "@【校验器】参数不合法";
        }

        return null;
    }

}
