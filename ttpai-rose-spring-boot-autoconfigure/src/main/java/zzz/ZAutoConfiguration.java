package zzz;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.Ordered;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/**
 * 最低优先级中，字母排序最低的优先级
 * <p>
 * 该类主要用于 Spring Boot 自动配置的顺序参考
 */
@AutoConfigureOrder(LOWEST_PRECEDENCE) // Spring Boot 1 & 2 的自动配置顺序是不一样的，这里明确指定以兼容统一行为
public class ZAutoConfiguration implements Ordered {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
