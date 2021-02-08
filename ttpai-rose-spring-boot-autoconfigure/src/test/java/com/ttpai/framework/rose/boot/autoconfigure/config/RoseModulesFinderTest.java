package com.ttpai.framework.rose.boot.autoconfigure.config;

import net.paoding.rose.web.impl.module.Module;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用于调试具体的代码方法
 *
 * @author Kail
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class RoseModulesFinderTest {

    @Resource
    private WebApplicationContext context;

    @Test
    public void prepareModules() throws Exception {
        final RoseModulesFinder roseModulesFinder = new RoseModulesFinder();
        final List<Module> modules = roseModulesFinder.prepareModules(context);
        System.out.println(modules);
    }

}
