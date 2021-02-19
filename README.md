

## 配置

- `ttpai.rose.boot.filter.patterns` 自定义 Filter 链接，默认 `/*`
- `ttpai.rose.boot.filter.ignore.paths` 自定义 Filter 忽略的链接，默认 `/views/*`、 `/favicon.ico`
- `ttpai.rose.boot.autoconfigure.amqp.primary` 解决 老的 Rose 项目下，无法配置 Primary ConnectionFactory 的问题（默认开启，配置为 false 关闭该功能）