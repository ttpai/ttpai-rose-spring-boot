package com.ttpai.framework.rose.boot.autoconfigure.config;

import lombok.Getter;
import net.paoding.rose.RoseFilter;
import net.paoding.rose.RoseVersion;
import net.paoding.rose.scanner.ModuleResource;
import net.paoding.rose.scanner.ModuleResourceProvider;
import net.paoding.rose.scanning.LoadScope;
import net.paoding.rose.util.PrinteHelper;
import net.paoding.rose.web.annotation.ReqMethod;
import net.paoding.rose.web.impl.mapping.ConstantMapping;
import net.paoding.rose.web.impl.mapping.Mapping;
import net.paoding.rose.web.impl.mapping.MappingNode;
import net.paoding.rose.web.impl.mapping.TreeBuilder;
import net.paoding.rose.web.impl.module.Module;
import net.paoding.rose.web.impl.module.ModulesBuilder;
import net.paoding.rose.web.impl.thread.LinkedEngine;
import net.paoding.rose.web.impl.thread.RootEngine;
import net.paoding.rose.web.instruction.InstructionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * @see RoseFilter#initFilterBean()
 *      * * * * * * *
 * @see RoseFilter#prepareRootApplicationContext()
 * @see RoseFilter#prepareModules(WebApplicationContext)
 * @see RoseFilter#prepareMappingTree(List)
 */
@Configuration
public class RoseModulesFinder implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RoseModulesFinder.class);

    @Resource
    private WebApplicationContext context;

    @Getter
    private List<Module> modules;

    @Getter
    private MappingNode mappingTree;

    private final LoadScope load = new LoadScope("", "controllers");

    /**
     * @see RoseFilter#prepareModules(WebApplicationContext)
     * @see RoseFilter#prepareMappingTree(List)
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        final long startTime = System.currentTimeMillis();

        // 1. 识别 Rose 程序模块
        modules = this.prepareModules(context);

        // 2. 创建匹配树以及各个结点的上的执行逻辑(Engine)
        mappingTree = this.prepareMappingTree(modules);

        final long cost = System.currentTimeMillis() - startTime;

        // 3. 打印概要信息
        String format = "[init] rose initialized, %s modules loaded, cost %sms! (version=%s)";
        String msg = String.format(format, modules.size(), cost, RoseVersion.getVersion());
        logger.info(msg);
        context.getServletContext().log(msg);

        // 4. 打印 modules 信息
        if (logger.isDebugEnabled()) {
            logger.debug(PrinteHelper.dumpModules(modules));
            logger.debug("mapping tree:\n {}", PrinteHelper.list(mappingTree));
        }

    }

    protected List<Module> prepareModules(WebApplicationContext rootContext) throws Exception {
        // 自动扫描识别web层资源，纳入Rose管理
        if (logger.isInfoEnabled()) {
            logger.info("[init/mudule] starting ...");
        }

        ModuleResourceProvider provider = this.springFactoriesFindFirst(ModuleResourceProvider.class);

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] using provider: {}", provider);
            logger.info("[init/module] call 'moduleResource': to find all module resources.");
            logger.info("[init/module] load {}", load);
        }
        List<ModuleResource> moduleResources = provider.findModuleResources(load);

        if (logger.isInfoEnabled()) {
            logger.info("[init/mudule] exits 'moduleResource'");
        }

        ModulesBuilder modulesBuilder = this.springFactoriesFindFirst(ModulesBuilder.class);

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] using modulesBuilder: {}", modulesBuilder);
            logger.info("[init/module] call 'moduleBuild': to build modules.");
        }

        List<Module> findModules = modulesBuilder.build(moduleResources, rootContext);

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] exits from 'moduleBuild'");
            logger.info("[init/mudule] found {} modules.", findModules.size());
        }

        return findModules;
    }

    protected MappingNode prepareMappingTree(List<Module> modules) {
        Mapping rootMapping = new ConstantMapping("");
        MappingNode mappingNode = new MappingNode(rootMapping);

        final InstructionExecutor instructionExecutor = this.springFactoriesFindFirst(InstructionExecutor.class);
        LinkedEngine rootEngine = new LinkedEngine(null, new RootEngine(instructionExecutor), mappingNode);
        mappingNode.getMiddleEngines().addEngine(ReqMethod.ALL, rootEngine);

        TreeBuilder treeBuilder = new TreeBuilder();
        treeBuilder.create(mappingNode, modules);

        return mappingNode;
    }

    protected <T> T springFactoriesFindFirst(Class<T> clazz) {

        final List<T> resourceProviders = SpringFactoriesLoader.loadFactories(
                clazz, Thread.currentThread().getContextClassLoader());

        if (resourceProviders.isEmpty()) {
            throw new IllegalArgumentException("SpringFactoriesLoader not Found " + clazz.getName());
        }
        final T instance = resourceProviders.get(0);
        if (resourceProviders.size() > 1) {
            logger.warn("SpringFactoriesLoader find Multi {}, Use {}", clazz.getName(), instance.getClass().getName());
        }
        return instance;
    }

}
