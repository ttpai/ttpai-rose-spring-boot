package net.paoding.rose.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 【严格校验】清除非 Spring MVC 的 Controller， 避免 Spring MVC 被 Rose 接管
 * <p>
 * 【为什么这个类在 net.paoding.rose.scanner 包下】
 * 【【【 因为 ModuleResourceProviderImpl 提供的 protected 方法，参数中有 Local，但是 Local 类是保内可见的】】】
 *
 * @author kail
 */
@Slf4j
public class BootModuleResourceProvider extends ModuleResourceProviderImpl {

    @Override
    protected void afterScanning(Local local) {
        for (ModuleResource moduleResource : local.moduleResourceMap.values()) {
            final List<Class<?>> moduleClasses = moduleResource.getModuleClasses();
            // 查找 Spring MVC 的 Controller
            final List<Class<?>> springMvcController = this.findSpringMvcController(moduleClasses);
            // 删除 Spring MVC 的 Controller
            moduleClasses.removeAll(springMvcController);
        }
        // 执行 父类的清除方法
        super.afterScanning(local);
    }

    /**
     * 查找 Spring MVC 的 Controller
     */
    protected List<Class<?>> findSpringMvcController(List<Class<?>> controllers) {
        List<Class<?>> springMvcController = new ArrayList<>();
        for (Class<?> controller : controllers) {
            // 判断该类是否是 Spring Mvc
            if (this.isSpringMvc(controller)) {
                springMvcController.add(controller);
            }
        }
        return springMvcController;
    }

    /**
     * 判断该类是否是 Spring Mvc【条件 Controller || RestController 存在】
     *
     * @see Controller
     * @see RestController
     */
    protected boolean isSpringMvc(Class<?> clazz) {
        final Controller controller = clazz.getAnnotation(Controller.class);
        final RestController restController = clazz.getAnnotation(RestController.class);
        return Objects.nonNull(controller) || Objects.nonNull(restController);
    }
}
