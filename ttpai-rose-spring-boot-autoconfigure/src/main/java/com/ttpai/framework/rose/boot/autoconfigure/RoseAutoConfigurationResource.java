package com.ttpai.framework.rose.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;

/**
 * 1. 支持 applicationContext*.xml 自动发现
 * -- 1.1 applicationContext-rose.xml Rose Web
 * -- 1.2 applicationContext-jade.xml Rose Dao
 * <p>
 * * @see PathMatchingResourcePatternResolver#addAllClassLoaderJarRoots 【jar:jar: 双重拼接】
 * * @see net.paoding.rose.scanning.context.RoseWebAppContext#getConfigResourcesThrows 【自定义实现，资源扫描】
 * <p>
 * 【AutoConfigure】默认是最低优先级，这里定义一个顺序，把 ImportResource 的加载拉的靠前一些
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 100)
@ImportResource({
        "classpath*:applicationContext*.xml", //
        // Resin 下必须明确文件名，使用通配符 PathMatchingResourcePatternResolver 无法匹配 jar 包中的文件
        // 因为 Resin 的 ClassLoader 获取的 Urls 返回的格式不对，会被 Spring 双重拼接 jar:jar:file:/xx/xxx/xx
        // Rose 只是通配的原因是其自定了实现了资源扫描
        "classpath*:applicationContext-rose.xml", //
        "classpath*:applicationContext-jade.xml", //
})
class RoseAutoConfigurationResource {

}
