
## Rose 迁移 Boot 步骤

- 增加 `com.ttpai.framework:ttpai-rose-spring-boot-starter:1.0.0-SNAPSHOT`
- 删除 `web.xml` 关于 `roseFilter` 的配置

## 限制条件

- 【TODO】Spring MVC 的 controller 要写在 `controller` 包下，不能写在 `controllers` 下，否则会被当做 rose 流程处理

## TODO

- 自动屏蔽 web.xml 中的 Rose 配置
