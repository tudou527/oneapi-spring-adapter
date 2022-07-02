# OneAPI Spring Adapter

![ci](https://github.com/tudou527/oneapi-spring-adapter/actions/workflows/workflow.yml/badge.svg)
[![codecov](https://codecov.io/gh/tudou527/oneapi-spring-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/tudou527/oneapi-spring-adapter)

> 这是一个 OneAPI 适配 Spring 规范的 HTTP 生产工具，帮助前端完善 API 工程化能力。

## 为什么会有这个项目
有 Swagger 珠玉在前，业界主要做的是 API 消费相关产品（如 API 管理平台)。Swagger 的原理是运行时从 Spring 上下文中拿到所有路由信息，由于依赖后端接入 Swagger，导致 API 生产/消费链路前端不可控。

## OneAPI 方案
`OneAPI` 的实现方案是静态解析（类似 AST），扫描项目下所有的 .java 文件，根据规范（本项目遵循的是 Spring 规范）识别 API 定义与相关的类型文件（用于 TS 场景），生成 JSON Schema 供前端消费。

基于 `OneAPI` 的静态解析，可以通过诸如 GitLab Hooks 便捷的实现 API 生产自动化：

<img src="https://raw.githubusercontent.com/tudou527/oneapi-spring-adapter/master/images/follow.png" width="760" />

## 使用
* 安装 JDK 1.8+ 及 Maven 环境
* Clone 后端代码并切换到你需要的分支
* 从右侧 [Packages](https://github.com/tudou527/oneapi-spring-adapter/packages/1516700) 页面下载 `spring-adapter-{version}.jar`
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

<details>
<summary>解析结果</summary>
<pre><code>
{
  "com.roncoo.education.bean.User": {
    "filePath": "/Users/oneapi/java/spring-boot-demo-02-1/src/main/java/com/roncoo/education/bean/User.java",
    "fileType": "RESOURCE",
    "packageName": "com.roncoo.education.bean",
    "description": {
      "text": "用户类",
      "tag": {
        "author": ["wujing"]
      }
    },
    "imports": ["java.util.Date", "com.roncoo.education.bean.User"],
    "class": {
      "name": "User",
      "classPath": "com.roncoo.education.bean.User",
      "description": {
        "text": "用户类",
        "tag": {
          "author": ["wujing"]
        }
      },
      "annotations": [],
      "isEnum": false,
      "isInterface": false,
      "isAbstract": false,
      "isPrivate": false,
      "isPublic": true,
      "fields": [{
        "name": "id",
        "type": {
          "name": "int",
          "classPath": "int"
        },
        "defaultValue": "",
        "isPrivate": true,
        "isPublic": false,
        "isProtected": false,
        "description": {
          "text": "用户 Id",
          "tag": {}
        },
        "annotations": []
      }, {
        "name": "name",
        "type": {
          "name": "String",
          "classPath": "java.lang.String"
        },
        "defaultValue": "",
        "isPrivate": true,
        "isPublic": false,
        "isProtected": false,
        "description": {
          "text": "用户名",
          "tag": {}
        },
        "annotations": []
      }, {
        "name": "date",
        "type": {
          "name": "Date",
          "classPath": "java.util.Date"
        },
        "defaultValue": "",
        "isPrivate": true,
        "isPublic": false,
        "isProtected": false,
        "description": {
          "text": "注册时间",
          "tag": {}
        },
        "annotations": []
      }],
      "methods": []
    }
  },
  "com.roncoo.education.controller.IndexController": {
    "filePath": "/Users/oneapi/java/spring-boot-demo-02-1/src/main/java/com/roncoo/education/controller/IndexController.java",
    "fileType": "ENTRY",
    "packageName": "com.roncoo.education.controller",
    "description": {
      "text": "spring-boot-demo-2-1",
      "tag": {
        "author": ["wujing"]
      }
    },
    "imports": ["java.util.Date", "java.util.HashMap", "org.springframework.web.bind.annotation.PathVariable", "org.springframework.web.bind.annotation.RequestMapping", "org.springframework.web.bind.annotation.RequestParam", "org.springframework.web.bind.annotation.RestController", "com.roncoo.education.bean.User", "com.roncoo.education.controller.IndexController"],
    "class": {
      "name": "IndexController",
      "classPath": "com.roncoo.education.controller.IndexController",
      "description": {
        "text": "spring-boot-demo-2-1",
        "tag": {
          "author": ["wujing"]
        }
      },
      "annotations": [{
        "name": "RestController",
        "classPath": "org.springframework.web.bind.annotation.RestController",
        "fields": []
      }, {
        "name": "RequestMapping",
        "classPath": "org.springframework.web.bind.annotation.RequestMapping",
        "fields": [{
          "name": "value",
          "type": "Constant",
          "array": false,
          "value": "/index"
        }]
      }],
      "isEnum": false,
      "isInterface": false,
      "isAbstract": false,
      "isPrivate": false,
      "isPublic": true,
      "fields": [],
      "methods": [{
        "name": "index",
        "description": {
          "tag": {}
        },
        "annotations": [{
          "name": "RequestMapping",
          "classPath": "org.springframework.web.bind.annotation.RequestMapping",
          "fields": []
        }],
        "parameters": [],
        "return": {
          "name": "String",
          "classPath": "java.lang.String"
        }
      }, {
        "name": "get",
        "description": {
          "tag": {}
        },
        "annotations": [{
          "name": "RequestMapping",
          "classPath": "org.springframework.web.bind.annotation.RequestMapping",
          "fields": [{
            "name": "value",
            "type": "Constant",
            "array": false,
            "value": "/get"
          }]
        }],
        "parameters": [{
          "name": "name",
          "type": {
            "name": "String",
            "classPath": "java.lang.String"
          },
          "description": {
            "tag": {}
          },
          "annotations": [{
            "name": "RequestParam",
            "classPath": "org.springframework.web.bind.annotation.RequestParam",
            "fields": []
          }]
        }],
        "return": {
          "name": "HashMap",
          "classPath": "java.util.HashMap",
          "items": [{
            "name": "String",
            "classPath": "java.lang.String"
          }, {
            "name": "Object",
            "classPath": "java.lang.Object"
          }]
        }
      }, {
        "name": "getUser",
        "description": {
          "tag": {}
        },
        "annotations": [{
          "name": "RequestMapping",
          "classPath": "org.springframework.web.bind.annotation.RequestMapping",
          "fields": [{
            "name": "value",
            "type": "Constant",
            "array": false,
            "value": "/get/{id}/{name}"
          }]
        }],
        "parameters": [{
          "name": "id",
          "type": {
            "name": "int",
            "classPath": "int"
          },
          "description": {
            "tag": {}
          },
          "annotations": [{
            "name": "PathVariable",
            "classPath": "org.springframework.web.bind.annotation.PathVariable",
            "fields": []
          }]
        }, {
          "name": "name",
          "type": {
            "name": "String",
            "classPath": "java.lang.String"
          },
          "description": {
            "tag": {}
          },
          "annotations": [{
            "name": "PathVariable",
            "classPath": "org.springframework.web.bind.annotation.PathVariable",
            "fields": []
          }]
        }],
        "return": {
          "name": "User",
          "classPath": "com.roncoo.education.bean.User"
        }
      }]
    }
  }
}
</code></pre>
</code>
</details>

