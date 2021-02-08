package com.ttpai.framework.rose.boot.autoconfigure;

import com.ttpai.framework.rose.boot.autoconfigure.config.RoseModulesFinder;
import com.ttpai.framework.rose.boot.autoconfigure.filter.RoseBootFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.DispatcherType;
import java.util.Collection;
import java.util.EnumSet;

/**
 * 1. 支持 applicationContext*.xml 自动发现
 * -- 1.1 applicationContext-rose.xml Rose Web
 * -- 1.2 applicationContext-jade.xml Rose Dao
 * <p>
 * 2. 配置 roseBootFilterRegistration
 */
@Configuration
@ImportResource("classpath*:applicationContext*.xml")
public class RoseAutoConfiguration {

    /**
     * 自定义过滤的 Url，可自定义哪些链接经过 Rose 过滤器
     */
    @Value("${rose.boot.filter.patterns:/*}")
    private Collection<String> patterns;

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
        return new RoseBootFilter(modules);
    }

    /**
     * Rose 过滤器
     */
    @Bean
    @ConditionalOnClass(name = "net.paoding.rose.RoseFilter")
    public FilterRegistrationBean roseBootFilterRegistration(RoseBootFilter filter) {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setUrlPatterns(patterns);
        bean.setDispatcherTypes(EnumSet.of(
                DispatcherType.REQUEST,
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE));
        return bean;
    }

}
