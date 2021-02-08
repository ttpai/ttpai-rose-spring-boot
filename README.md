
## TODO

- 【√】RoseBootFilter 会初始化两次
- 自动屏蔽 web.xml 中的 Rose 配置
- 无需增加 Spring Boot 启动类


## 功能

- `rose.boot.filter.patterns` 自定义 Filter 链接，默认 `/*`
- `rose.boot.filter.ignore.paths` 自定义 Filter 忽略的链接，默认 `/views/*`、 `/favicon.ico`