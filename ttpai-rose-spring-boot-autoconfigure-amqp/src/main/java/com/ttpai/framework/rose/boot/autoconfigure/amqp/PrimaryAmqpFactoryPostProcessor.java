package com.ttpai.framework.rose.boot.autoconfigure.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
class PrimaryAmqpFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final LinkedHashMap<String, BeanDefinition> connections = this.findNoPrimaryConnectionFactory(beanFactory);
        if (connections.size() <= 1) {
            return;
        }

        final Optional<Map.Entry<String, BeanDefinition>> entry = connections.entrySet().stream().findFirst();
        log.warn( //
                "[ ttpai framework ] Find {} o.s.amqp.rabbit.c.ConnectionFactory, Set  {} Primary",
                connections.size(), entry.get().getKey());
        entry.get().getValue().setPrimary(true);
    }

    /**
     * 查找没有 Primary 的 ConnectionFactory
     *
     * @return 只要有一个 ConnectionFactory 是 Primary，返回空集合
     */
    private LinkedHashMap<String, BeanDefinition> findNoPrimaryConnectionFactory(ConfigurableListableBeanFactory beanFactory) {
        LinkedHashMap<String, BeanDefinition> connectionFactories = new LinkedHashMap<>();

        final String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            // 是 ConnectionFactory
            if (isConnectionFactory(beanDefinition)) {
                // 只要有一个 ConnectionFactory 是 Primary
                if (beanDefinition.isPrimary()) {
                    // 返回空集合
                    connectionFactories.clear();
                    return connectionFactories;
                }
                connectionFactories.put(beanDefinitionName, beanDefinition);
            }
        }
        return connectionFactories;
    }

    private boolean isConnectionFactory(BeanDefinition beanDefinition) {
        Class<?> beanClass = null;
        //
        if (beanDefinition instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition definition = (AbstractBeanDefinition) beanDefinition;
            // getBeanClass 开始的时候是 String， 并非真正的 Class
            if (definition.hasBeanClass()) {
                beanClass = definition.getBeanClass();
            }
        }
        //
        if (null == beanClass) {
            final String beanClassName = beanDefinition.getBeanClassName();
            if (null == beanClassName) {
                return false;
            }
            try {
                beanClass = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new NoSuchBeanDefinitionException(beanClassName, e.getMessage());
            }
        }

        // ConnectionFactory 是 beanClass 的父类
        return ConnectionFactory.class.isAssignableFrom(beanClass);
    }

}
