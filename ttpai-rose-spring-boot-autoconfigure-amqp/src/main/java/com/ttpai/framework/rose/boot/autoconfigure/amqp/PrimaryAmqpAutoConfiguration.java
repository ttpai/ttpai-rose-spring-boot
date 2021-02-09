package com.ttpai.framework.rose.boot.autoconfigure.amqp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 主要解决 老的 Rose 项目下，无法配置 Primary ConnectionFactory 的问题
 * <p>
 * 找到第一个 ConnectionFactory 并设置为 Primary
 */
@Configuration
public class PrimaryAmqpAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "rose.boot.autoconfigure.amqp.primary", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.connection.ConnectionFactory")
    public PrimaryAmqpFactoryPostProcessor primaryAmqpFactoryPostProcessor() {
        return new PrimaryAmqpFactoryPostProcessor();
    }

}
