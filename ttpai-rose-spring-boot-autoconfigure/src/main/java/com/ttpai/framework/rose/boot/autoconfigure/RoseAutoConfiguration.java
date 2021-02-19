package com.ttpai.framework.rose.boot.autoconfigure;

import com.ttpai.framework.rose.boot.autoconfigure.config.RoseModulesFinder;
import com.ttpai.framework.rose.boot.autoconfigure.filter.RoseBootFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * 1. 支持 applicationContext*.xml 自动发现
 * -- 1.1 applicationContext-rose.xml Rose Web
 * -- 1.2 applicationContext-jade.xml Rose Dao
 * <p>
 * 2. 配置 roseBootFilterRegistration
 *
 * @see PathMatchingResourcePatternResolver#addAllClassLoaderJarRoots 【jar:jar: 双重拼接】
 * @see net.paoding.rose.scanning.context.RoseWebAppContext#getConfigResourcesThrows 【自定义实现，资源扫描】
 */
@Lazy(value = false)
@Configuration
@ImportResource({
        "classpath*:applicationContext*.xml", //
        // Resin 下必须明确文件名，使用通配符 PathMatchingResourcePatternResolver 无法匹配 jar 包中的文件
        // 因为 Resin 的 ClassLoader 获取的 Urls 返回的格式不对，会被 Spring 双重拼接 jar:jar:file:/xx/xxx/xx
        // Rose 只是通配的原因是其自定了实现了资源扫描
        "classpath*:applicationContext-rose.xml", //
        "classpath*:applicationContext-jade.xml", //
})
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
     */
    @Bean
    @Lazy(value = false)
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
