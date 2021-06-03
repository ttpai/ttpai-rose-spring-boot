# ttpai-rose-spring-boot



## 接入步骤

### 1 增加 ttpai-rose-spring-boot-starter 依赖

```xml
<dependency>
  <groupId>cn.ttpai.framework</groupId>
  <artifactId>ttpai-rose-spring-boot-starter</artifactId>
  <version>最新版本</version>
</dependency>
```



### 2 删除 web.xml 中的 Rose 配置

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



### 3 增加 Spring Boot 启动类

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



## 配置选项

- `ttpai.rose.boot.filter.patterns` 自定义 Filter 链接，默认 `/*`

- `ttpai.rose.boot.filter.ignore.paths` 自定义 Filter 忽略的链接，默认 `/views/*`、 `/favicon.ico`

- `ttpai.rose.boot.autoconfigure.amqp.primary` 解决 老的 Rose 项目下，无法配置 Primary ConnectionFactory 的问题（默认开启，配置为 false 关闭该功能）

  



## 注意事项

- Spring MVC 的 controller 要写在 controller 包下，不能写在 controllers 下，否则会被当做 rose 流程处理
- Rose 代码 和 SpringMVC 代码的拦截器是不兼容，迁移成 Spring MVC 之后，请为其增加拦截器逻辑
- main 方法启动时不支持 jsp， 如果有使用 jsp 页面，请使用 Web容器 启动



## 常见问题

### 多数据源时：required a single bean, but 'n' were found:

#### 解决方法 1

```xml
<!-- 增加 primary="true" -->
<bean id="jade.dataSource.com.ttpai.api" parent="abstractDruidDataSource" destroy-method="close" primary="true">
  <property name="url" value="${boss.jdbc.url}"/>
  <property name="username" value="${boss.jdbc.username}"/>
  <property name="password" value="${boss.jdbc.password}"/>
  ...
</bean>
```
#### 解决方法 2

使用 ttpai-mybatis-starter

```xml
<dependency>
  <groupId>cn.ttpai.framework</groupId>
  <artifactId>ttpai-mybatis-starter</artifactId>
  <version>最新版本</version>
</dependency>
```



### 使用 jsp

1. 在 application.properties 增加以下配置


```properties
# jsp 文件路径，这里使用 Rose 的查找路径
spring.mvc.view.prefix=/views/
# 视图后缀
spring.mvc.view.suffix=.jsp
# 禁用 favicon 图标
spring.mvc.favicon.enabled=false
```
2. 注意 Spring Controller 的接口方法注解，检查是否是 @Get、@Post，这个是 Rose 的注解，不是 Spring MVC 的注解

3. 不要运行 Application 的 main 方法启动，否则 jsp 失效，要使用 Resin、Tomcat 等 Web 容器启动


