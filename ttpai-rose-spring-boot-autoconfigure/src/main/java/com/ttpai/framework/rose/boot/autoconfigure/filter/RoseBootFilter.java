package com.ttpai.framework.rose.boot.autoconfigure.filter;

import com.ttpai.framework.rose.boot.autoconfigure.config.RoseModulesFinder;
import lombok.extern.slf4j.Slf4j;
import net.paoding.rose.RoseConstants;
import net.paoding.rose.RoseFilter;
import net.paoding.rose.scanning.context.RoseWebAppContext;
import net.paoding.rose.web.RequestPath;
import net.paoding.rose.web.impl.mapping.ignored.*;
import net.paoding.rose.web.impl.module.ControllerRef;
import net.paoding.rose.web.impl.module.Module;
import net.paoding.rose.web.impl.thread.Rose;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @see RoseFilter
 * @see RoseWebAppContext
 */
@Slf4j
public class RoseBootFilter extends GenericFilterBean {

    private final RoseModulesFinder roseTree;

    public RoseBootFilter(RoseModulesFinder roseTree) {
        this.roseTree = roseTree;
    }

    private IgnoredPath[] ignoredPaths = new IgnoredPath[]{
            new IgnoredPathStarts(RoseConstants.VIEWS_PATH_WITH_END_SEP),
            new IgnoredPathEquals("/favicon.ico")
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {

        if (!(req instanceof HttpServletRequest && resp instanceof HttpServletResponse)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 创建 RequestPath 对象，用于记录对地址解析的结果
        final RequestPath requestPath = new RequestPath(request);

        // 简单、快速判断本次请求，如果不应由 Rose 执行，返回 true
        if (quicklyPass(requestPath)) {
            notMatched(chain, request, response, requestPath);
            return;
        }

        // matched 为 true 代表本次请求被 Rose 匹配，不需要转发给容器的其他 Filter 或 Servlet
        boolean matched = false;
        try {
            // rose 对象代表 Rose 框架对一次请求的执行：一朵玫瑰出墙来
            final Rose rose = new Rose(roseTree.getModules(), roseTree.getMappingTree(),
                    request, response, requestPath);

            // 对请求进行匹配、处理、渲染以及渲染后的操作，如果找不到映配则返回false
            matched = rose.start();
        } catch (Throwable exception) {
            throwServletException(requestPath, exception);
        }

        // 非 Rose 的请求转发给 WEB 容器的其他组件处理，而且不放到上面的 try-catch 块中
        if (!matched) {
            notMatched(chain, request, response, requestPath);
        }
    }

    /**
     * 简单、快速判断本次请求
     */
    protected boolean quicklyPass(final RequestPath requestPath) {
        for (IgnoredPath ignoredPath : ignoredPaths) {
            if (ignoredPath.hit(requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 抛出异常，打印异常信息
     */
    protected void throwServletException(RequestPath requestPath, Throwable exception) throws ServletException {
        String msg = requestPath.getMethod() + " " + requestPath.getUri();
        ServletException servletException;
        if (exception instanceof ServletException) {
            servletException = (ServletException) exception;
        } else {
            servletException = new NestedServletException(msg, exception);
        }
        log.error(msg, exception);
        getServletContext().log(msg, exception);
        throw servletException;
    }

    /**
     * 没有匹配到，调用其他 Filter
     */
    protected void notMatched(FilterChain chain, HttpServletRequest req, HttpServletResponse resp,
                              RequestPath path) throws IOException, ServletException {

        if (log.isDebugEnabled()) {
            log.debug("not rose uri: " + path.getUri());
        }
        // 调用其它 Filter
        chain.doFilter(req, resp);
    }

    /**
     * 自定义忽略的链接
     */
    public void setIgnoredPaths(String[] ignoredPathStrings) {
        List<IgnoredPath> list = new ArrayList<>(ignoredPathStrings.length + 2);
        for (String ignoredPath : ignoredPathStrings) {
            ignoredPath = ignoredPath.trim();
            if (StringUtils.isEmpty(ignoredPath)) {
                continue;
            }
            /*
             * 忽略所有
             */
            if (ignoredPath.equals("*")) {
                list.add(new IgnoredPathEquals(""));
                list.add(new IgnoredPathStarts("/"));
                break;
            }
            /*
             * 正则忽略
             */
            if (ignoredPath.startsWith("regex:")) {
                list.add(new IgnoredPathRegexMatch(ignoredPath.substring("regex:".length())));
            } else {
                // 补全
                if (ignoredPath.length() > 0 && !ignoredPath.startsWith("/") && !ignoredPath.startsWith("*")) {
                    ignoredPath = "/" + ignoredPath;
                }
                //
                if (ignoredPath.endsWith("*")) {
                    list.add(new IgnoredPathStarts(ignoredPath.substring(0, ignoredPath.length() - 1)));
                } else if (ignoredPath.startsWith("*")) {
                    list.add(new IgnoredPathEnds(ignoredPath.substring(1)));
                } else {
                    list.add(new IgnoredPathEquals(ignoredPath));
                }
            }
        }

        IgnoredPath[] paths = Arrays.copyOf(this.ignoredPaths, this.ignoredPaths.length + list.size());
        for (int i = this.ignoredPaths.length; i < paths.length; i++) {
            paths[i] = list.get(i - this.ignoredPaths.length);
        }
        this.ignoredPaths = paths;
    }

    /**
     * @see net.paoding.rose.web.portal.impl.PortalWaitInterceptor#waitForWindows
     * @see net.paoding.rose.web.portal.impl.PortalWaitInterceptor
     */
    protected void supportsRosePipe(final HttpServletRequest httpRequest) {
        // 这个代码为 rosepipe 所用，以避免 rosepipe 的"Cannot forward after response has been committed"异常
        Object window = httpRequest.getAttribute(RoseConstants.WINDOW_ATTR);
        if (window != null && window.getClass().getName().startsWith("net.paoding.rose.web.portal")) {
            httpRequest.setAttribute(RoseConstants.PIPE_WINDOW_IN, Boolean.TRUE);

            if (log.isDebugEnabled()) {
                try {
                    log.debug("notify window '{}'", httpRequest.getAttribute("$$paoding-rose-portal.window.name"));
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            synchronized (window) {
                window.notifyAll();
            }
        }
    }



    protected void removeMvc(List<Module> modules) {
        final Iterator<Module> iterator = modules.iterator();
        for (; iterator.hasNext(); ) {
            final Module module = iterator.next();

            final List<ControllerRef> rmControllers = new ArrayList<>();
            final List<ControllerRef> controllers = module.getControllers();
            final Iterator<ControllerRef> controllerRefIterator = controllers.iterator();
            for (; controllerRefIterator.hasNext(); ) {
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

}
