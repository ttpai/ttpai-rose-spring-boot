

## 配置

- `ttpai.rose.boot.filter.patterns` 自定义 Filter 链接，默认 `/*`
- `ttpai.rose.boot.filter.ignore.paths` 自定义 Filter 忽略的链接，默认 `/views/*`、 `/favicon.ico`
- `ttpai.rose.boot.autoconfigure.amqp.primary` 解决 老的 Rose 项目下，无法配置 Primary ConnectionFactory 的问题（默认开启，配置为 false 关闭该功能）

## 版本

### v1.0.0

- 支持 Rose 迁移 Spring Boot 初始版本
- 接入文档： http://confluence.ttpai.cn/pages/viewpage.action?pageId=9592094
- 示例： ttpai-rose-spring-boot-example