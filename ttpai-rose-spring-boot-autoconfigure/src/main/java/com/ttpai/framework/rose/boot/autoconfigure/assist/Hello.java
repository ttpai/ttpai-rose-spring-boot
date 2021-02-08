package com.ttpai.framework.rose.boot.autoconfigure.assist;

import net.paoding.rose.web.impl.module.ControllerRef;
import net.paoding.rose.web.impl.module.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hello {


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

}
