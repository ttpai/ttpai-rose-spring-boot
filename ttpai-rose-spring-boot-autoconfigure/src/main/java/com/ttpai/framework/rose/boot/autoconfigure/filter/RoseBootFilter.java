package com.ttpai.framework.rose.boot.autoconfigure.filter;

import lombok.extern.slf4j.Slf4j;
import net.paoding.rose.RoseFilter;
import net.paoding.rose.RoseVersion;
import net.paoding.rose.scanner.ModuleResource;
import net.paoding.rose.scanner.ModuleResourceProvider;
import net.paoding.rose.scanner.ModuleResourceProviderImpl;
import net.paoding.rose.scanning.LoadScope;
import net.paoding.rose.scanning.context.RoseWebAppContext;
import net.paoding.rose.web.RequestPath;
import net.paoding.rose.web.annotation.ReqMethod;
import net.paoding.rose.web.impl.mapping.ConstantMapping;
import net.paoding.rose.web.impl.mapping.Mapping;
import net.paoding.rose.web.impl.mapping.MappingNode;
import net.paoding.rose.web.impl.mapping.TreeBuilder;
import net.paoding.rose.web.impl.module.ControllerRef;
import net.paoding.rose.web.impl.module.Module;
import net.paoding.rose.web.impl.module.ModulesBuilder;
import net.paoding.rose.web.impl.module.ModulesBuilderImpl;
import net.paoding.rose.web.impl.thread.LinkedEngine;
import net.paoding.rose.web.impl.thread.RootEngine;
import net.paoding.rose.web.impl.thread.Rose;
import net.paoding.rose.web.instruction.InstructionExecutor;
import net.paoding.rose.web.instruction.InstructionExecutorImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @see RoseFilter
 * @see RoseWebAppContext
 */
@Slf4j
@Configuration
public class RoseBootFilter extends OncePerRequestFilter {

    private final LoadScope load = new LoadScope("", "controllers");

    private Class<? extends ModuleResourceProvider> moduleResourceProviderClass = ModuleResourceProviderImpl.class;

    private Class<? extends ModulesBuilder> modulesBuilderClass = ModulesBuilderImpl.class;

    private InstructionExecutor instructionExecutor = new InstructionExecutorImpl();

    List<Module> modules;

    MappingNode mappingTree;

    @Resource
    private WebApplicationContext context;

    @Override
    public void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                 FilterChain filterChain) throws ServletException, IOException {
        // 创建RequestPath对象，用于记录对地址解析的结果
        final RequestPath requestPath = new RequestPath(httpRequest);

        // // 简单、快速判断本次请求，如果不应由Rose执行，返回true
        // if (quicklyPass(requestPath)) {
        // notMatched(filterChain, httpRequest, httpResponse, requestPath);
        // return;
        // }

        // matched为true代表本次请求被Rose匹配，不需要转发给容器的其他 flter 或 servlet
        boolean matched = false;
        try {
            // rose 对象代表Rose框架对一次请求的执行：一朵玫瑰出墙来
            final Rose rose = new Rose(modules, mappingTree, httpRequest, httpResponse, requestPath);

            // 对请求进行匹配、处理、渲染以及渲染后的操作，如果找不到映配则返回false
            matched = rose.start();

        } catch (Throwable exception) {
            throwServletException(requestPath, exception);
        }

        // 非Rose的请求转发给WEB容器的其他组件处理，而且不放到上面的try-catch块中
        if (!matched) {
            notMatched(filterChain, httpRequest, httpResponse, requestPath);
        }
    }

    /**
     * @see RoseFilter#initFilterBean()
     */
    @Override
    protected void initFilterBean() throws ServletException {
        final ServletContext servletContext = super.getServletContext();
        final Environment environment = super.getEnvironment();
        final FilterConfig filterConfig = super.getFilterConfig();

        // roseFilter = new RoseFilter();

        // RoseWebAppContext rootContext = new RoseWebAppContext(servletContext, load, false);

        try {

            // WebApplicationContext rootContext = roseFilter.prepareRootApplicationContext();

            // 识别 Rose 程序模块
            modules = this.prepareModules(context);

            // 删除 MVC
            // removeMvc(modules);

            // 创建匹配树以及各个结点的上的执行逻辑(Engine)
            mappingTree = this.prepareMappingTree(modules);

            System.out.println(mappingTree);

        } catch (Exception e) {
            StringBuilder sb = new StringBuilder(1024);
            sb.append("[Rose-").append(RoseVersion.getVersion());
            sb.append("@Spring-").append(SpringVersion.getVersion()).append("]:");
            sb.append(e.getMessage());
            logger.error(sb.toString(), e);
            throw new NestedServletException(sb.toString(), e);
        }
    }

    // private boolean quicklyPass(final RequestPath requestPath) {
    // for (IgnoredPath p : ignoredPaths) {
    // if (p.hit(requestPath)) {
    // return true;
    // }
    // }
    // return false;
    // }

    protected void removeMvc(List<Module> modules) {
        final Iterator<Module> iterator = modules.iterator();
        for (; iterator.hasNext();) {
            final Module module = iterator.next();

            final List<ControllerRef> rmControllers = new ArrayList<>();
            final List<ControllerRef> controllers = module.getControllers();
            final Iterator<ControllerRef> controllerRefIterator = controllers.iterator();
            for (; controllerRefIterator.hasNext();) {
                final ControllerRef controllerRef = controllerRefIterator.next();
                final Class<?> controllerClass = controllerRef.getControllerClass();
                if (null != controllerClass.getAnnotation(Controller.class)
                        || null != controllerClass.getAnnotation(RestController.class)) {
                    rmControllers.add(controllerRef);
                }
            }

            controllers.removeAll(rmControllers);

            // if (module.getControllers().isEmpty()){
            // modules.remove(module);
            // }

        }
    }

    protected List<Module> prepareModules(WebApplicationContext rootContext) throws Exception {
        // 自动扫描识别web层资源，纳入Rose管理
        if (logger.isInfoEnabled()) {
            logger.info("[init/mudule] starting ...");
        }

        ModuleResourceProvider provider = moduleResourceProviderClass.newInstance();

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] using provider: " + provider);
            logger.info("[init/module] call 'moduleResource': to find all module resources.");
            logger.info("[init/module] load " + load);
        }
        List<ModuleResource> moduleResources = provider.findModuleResources(load);

        if (logger.isInfoEnabled()) {
            logger.info("[init/mudule] exits 'moduleResource'");
        }

        ModulesBuilder modulesBuilder = modulesBuilderClass.newInstance();

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] using modulesBuilder: " + modulesBuilder);
            logger.info("[init/module] call 'moduleBuild': to build modules.");
        }

        List<Module> modules = modulesBuilder.build(moduleResources, rootContext);

        if (logger.isInfoEnabled()) {
            logger.info("[init/module] exits from 'moduleBuild'");
            logger.info("[init/mudule] found " + modules.size() + " modules.");
        }

        return modules;
    }

    private MappingNode prepareMappingTree(List<Module> modules) {
        Mapping rootMapping = new ConstantMapping("");
        MappingNode mappingTree = new MappingNode(rootMapping);
        LinkedEngine rootEngine = new LinkedEngine(null, new RootEngine(instructionExecutor), mappingTree);
        mappingTree.getMiddleEngines().addEngine(ReqMethod.ALL, rootEngine);

        TreeBuilder treeBuilder = new TreeBuilder();
        treeBuilder.create(mappingTree, modules);

        return mappingTree;
    }

    private void throwServletException(RequestPath requestPath, Throwable exception) throws ServletException {
        String msg = requestPath.getMethod() + " " + requestPath.getUri();
        ServletException servletException;
        if (exception instanceof ServletException) {
            servletException = (ServletException) exception;
        } else {
            servletException = new NestedServletException(msg, exception);
        }
        logger.error(msg, exception);
        getServletContext().log(msg, exception);
        throw servletException;
    }

    protected void notMatched(//
                              FilterChain filterChain, //
                              HttpServletRequest httpRequest,//
                              HttpServletResponse httpResponse,//
                              RequestPath path)//
            throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("not rose uri: " + path.getUri());
        }
        // 调用其它Filter
        filterChain.doFilter(httpRequest, httpResponse);
    }

}
