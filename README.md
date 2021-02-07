## Rose 迁移 Boot 步骤

### 1. 增加 ttpai-rose-spring-boot-starter 依赖

```xml

<dependency>
    <groupId>com.ttpai.framework</groupId>
    <artifactId>ttpai-rose-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 删除 web.xml 中的 Rose 配置

```xml

<filter>
    <filter-name>roseFilter</filter-name>
    <filter-class>net.paoding.rose.RoseFilter</filter-class>
</filter>
<filter-mapping>
<filter-name>roseFilter</filter-name>
<url-pattern>/*</url-pattern>
<dispatcher>REQUEST</dispatcher>
<dispatcher>FORWARD</dispatcher>
<dispatcher>INCLUDE</dispatcher>
</filter-mapping>
```

### 3. 增加 Spring Boot 启动类

```java

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(Application.class);
        return super.configure(builder);
    }
}
```

## 限制条件

- 【TODO】Spring MVC 的 controller 要写在 `controller` 包下，不能写在 `controllers` 下，否则会被当做 rose 流程处理

## TODO

- RoseBootFilter 会初始化两次
- 多数据源时必须增加 `primary="true"`
- 自动屏蔽 web.xml 中的 Rose 配置
- 【×】无需增加 Spring Boot 启动类
