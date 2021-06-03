# Change Log



### v1.0.2 2021-06-03

- AutoConfig 顺序优化，提高兼容性
- com.ttpai --》 cn.ttpai ，发布中央仓库



### v1.0.1 2021-03-19

- 解决与 ttpai-mybatis-starter 的冲突问题
    - 拆分 RoseAutoConfigurationResource（applicationContext*.xml 自动配置） 和 RoseAutoConfiguration（RoseBootFilter）
    - 与 ttpai-mybatis-starter 同时存在时的 自动配置 顺序如下
        1. 【-100】`RoseAutoConfigurationResource` >> .xml 配置文件先解析，便于后续获取 Bean 的条件定义
        2. 【0】`MyBatisMultiDataSourceProcessorConfigure` >> 条件注解，获取 DataSource 的定义，扫描 Mapper 等，解决 Rose 对 Mapper 的依赖
        3. 【100】`RoseAutoConfiguration` >> 初始化 Rose



### v1.0.0

- 支持 Rose 迁移 Spring Boot 初始版本
- 示例： ttpai-rose-spring-boot-example
