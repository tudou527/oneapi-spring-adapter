# OneAPI Spring Adapter

![ci](https://github.com/tudou527/oneapi-spring-adapter/actions/workflows/workflow.yml/badge.svg)
[![codecov](https://codecov.io/gh/tudou527/oneapi-spring-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/tudou527/oneapi-spring-adapter)

> Spring 项目（包括 Spring Boot/Spring MVC）RESTful API 生成工具

## 与 Swagger 的差异

* 业务代码 0 侵入
* 不需要启动应用（类似 “AST” 的方案）
* 支持读取 JSDoc 作注释
* 支持企业内部 RPC 协议（非 RESTful API）
* 支持识别 source jar
* 支持通过反编译 jar 识别字段类型


## 如何使用
* 安装 JDK 1.8+ 及 Maven 环境
* Clone 后端代码并切换到你需要的分支
* 从右侧 [Packages](https://github.com/tudou527?tab=packages&repo_name=oneapi-spring-adapter) 页面下载 `spring-adapter-{version}.jar`
* 运行 `spring-adapter` 执行 jar
  * 参数：
    * `project`：后端项目地址
    * `repository`：本地 Maven 缓存目录
    * `output`：解析结果 JSON Schema 文件保存目录

### 示例
测试项目来源：[roncoo/spring-boot-demo](https://github.com/roncoo/spring-boot-demo/blob/master/spring-boot-demo-02-1/src/main/java/com/roncoo/education/controller/IndexController.java)

```
# 解析 spring-boot-demo-02-1/ 中所有的 HTTP API 及类型文件，生成 result.json 保存到 /Users/oneapi/ 目录
java -jar ./spring-adapter-1.0.0.jar -project=/Users/oneapi/java/spring-boot-demo-02-1 -repository=/Users/oneapi/.m2/repository -output=/Users/oneapi/
```

* [解析结果示例](./attach/result.json)
* [result.json 类型定义](./attach/JavaMeta.d.ts)

## TODO
- [ ] 提供 OpenAPI 协议转换工具
- [ ] 提供生成前端 Service 工具
