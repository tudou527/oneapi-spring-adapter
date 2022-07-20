# OneAPI Spring Adapter

![ci](https://github.com/tudou527/oneapi-spring-adapter/actions/workflows/ci.yml/badge.svg)
[![codecov](https://codecov.io/gh/tudou527/oneapi-spring-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/tudou527/oneapi-spring-adapter)

> Spring 项目（Spring Boot/Spring MVC）RESTful API 生成工具

## 与 Swagger 的差异
* 不需要修改后端代码
* 不需要启动应用
* 支持读取 JSDoc 作注释
* 支持通过源码 jar、反编译等方式补充类型信息
* 支持企业内部 RPC 协议（非 RESTful API）

## 与 YApi、Apifox 等差异
* API 生产与消费的区别


## 如何使用
* 安装 JDK 1.8+ 及 Maven 环境
* Clone 后端代码并切换到你需要的分支
    * 为了获得完整的类型信息，推荐执行一次 `mvn dependency:sources install -Dmaven.test.skip=true` 安装完整的依赖
* 从右侧 [Packages](https://github.com/tudou527?tab=packages&repo_name=oneapi-spring-adapter) 页面下载 `spring-adapter-{version}.jar`
* 运行 `spring-adapter` 执行 jar
  * 参数：
    * `project`：后端项目地址
    * `output`：解析结果 JSON Schema 文件保存目录
    * `repository` 本地 Maven 缓存目录，可选 默认 `~/.m2`

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
