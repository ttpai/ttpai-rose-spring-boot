package com.ttpai.framework.rose.boot.autoconfigure;

import com.ttpai.framework.rose.boot.autoconfigure.config.RoseModulesFinder;
import com.ttpai.framework.rose.boot.autoconfigure.filter.RoseBootFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * 配置 roseBootFilterRegistration
 */
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 100)
class RoseAutoConfiguration {

    /**
     * 自定义过滤的 Url，可自定义哪些链接经过 Rose 过滤器
     */
    @Value("${ttpai.rose.boot.filter.patterns:/*}")
    private String patterns;

    @Value("${ttpai.rose.boot.filter.ignore.paths:}")
    private String ignorePaths;

    @Bean
    @ConditionalOnClass(name = "net.paoding.rose.RoseFilter")
    public RoseModulesFinder roseModulesFinder() {
        return new RoseModulesFinder();
    }

    /**
     * 自定义 Rose 过滤器
     */
    @Bean
    @ConditionalOnClass(name = "net.paoding.rose.RoseFilter")
    public RoseBootFilter roseBootFilter(RoseModulesFinder modules) {
        final RoseBootFilter roseBootFilter = new RoseBootFilter(modules);
        if (StringUtils.isNotBlank(ignorePaths)) {
            roseBootFilter.setIgnoredPaths(ignorePaths.split(","));
        }
        return new RoseBootFilter(modules);
    }

    /**
     * Rose 过滤器
     *
     * @see ServletContextInitializer
     */
    @Bean
    @ConditionalOnClass(name = "net.paoding.rose.RoseFilter")
    public FilterRegistrationBean roseBootFilterRegistration(RoseBootFilter filter) {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        //
        bean.setUrlPatterns(Arrays.asList(patterns.split(",")));
        //
        bean.setDispatcherTypes(EnumSet.of(
                DispatcherType.REQUEST,
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE));
        return bean;
    }

}
