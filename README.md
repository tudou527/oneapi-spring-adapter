# OneAPI Spring Adapter

![ci](https://github.com/tudou527/oneapi-spring-adapter/actions/workflows/ci.yml/badge.svg)
[![codecov](https://codecov.io/gh/tudou527/oneapi-spring-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/tudou527/oneapi-spring-adapter)

## 开发
* clone 代码
* 执行 `sh ./intall.sh` 安装独立 jar 依赖
* 启动类：`com.oneapi.spring.Application`，参数如下：
  * -project: 必须，后端代码目录（绝对路径）
  * -output: 必须，解析结果保存路径（绝对路径）
  * -repository: 可选，本地 mvn 包仓库（默认 ~/.m2），会从这里反编译所有 jar 用于补全类型信息
* 覆盖率：`mvn test`
  * 覆盖率报告：/target/site/jacoco/index.html
* 打包：`mvn package`
* 其他
  * 解析结果示例：./attach/result.json
  * result.json 类型定义：./attach/JavaMeta.d.ts

## BadCase
  * /org/apache/commons/commons-pool2/2.11.1/commons-pool2-2.11.1-sources.jar
    * org.apache.commons.pool2.impl.GenericObjectPool#L104
      * IdentityWrapper 类型来自于继承类 BaseGenericObjectPool 中的定义