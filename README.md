# OneAPI Java 解析器

## 相关文档
- [Mockito](https://www.letianbiji.com/java-mockito/mockito-hello-world.html)

## 初衷
API 生产端目前在跑的解析器语言是 Java 代码，前端维护起来有点成本。所以把原来的逻辑做了一次精简，从原来的解析整个项目改成了只支持解析单个文件，返回的元数据再在 JS 中二次加工为符合任何协议的 Schema 数据。


### 返回数据格式例子
```
{
  "filePath": "/Users/xiaoyun/workspace/sofaboot3-demo/app/web/src/main/java/com/oneapi/demo/web/home/common/WebConstants.java",
  "packageName": "com.oneapi.demo.web.home.common",
  "description": {
    "text": "Copyright (c) 2004-2020 All Rights Reserved."
  },
  "imports": [],
  "classList": [{
    "name": "WebConstants",
    "classPath": "com.oneapi.demo.web.home.common.WebConstants",
    "type": [],
    "description": {
      "text": "WEB常量类\n主要用来统一规划各模块的URL路径",
      "tag": {
        "author": "xingyan.djx",
        "version": "$Id: WebConstants.java, v 0.1 2020年04月07日 4:39 PM xingyan.djx Exp $"
      }
    },
    "isEnum": false,
    "isInterface": false,
    "isAbstract": false,
    "isPrivate": false,
    "isPublic": true,
    "fields": [{
      "name": "API_ROOT_PATH",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/api\"",
      "description": {
        "text": "接口定义"
      }
    }, {
      "name": "QUERY_LIST",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/query/list\"",
      "description": {}
    }, {
      "name": "QUERY_BY_KEY",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/query/bykey\"",
      "description": {}
    }, {
      "name": "UPDATE",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/update\"",
      "description": {}
    }, {
      "name": "ADD",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/add\"",
      "description": {}
    }, {
      "name": "ABILITY_CENTER",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "API_ROOT_PATH + \"/abilitycenter\"",
      "description": {}
    }, {
      "name": "TECH_PROD",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/techproduct\"",
      "description": {}
    }, {
      "name": "FUNCTION",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/function\"",
      "description": {}
    }, {
      "name": "SERVICE",
      "type": {
        "name": "java.lang.String"
      },
      "defaultValue": "\"/service\"",
      "description": {}
    }],
    "methods": []
  }]
}
```

## 进度

* ✅  package 相关
  * ✅  基础信息
  * ✅  描述
  * ✅  import 列表
* ✅  class
  * ✅  基础信息
    * ✅  继承、扩展信息
  * ✅  描述
  * ✅  字段
  * ✅  方法
    * ✅  入参
    * ✅  返回值
* ❎  类型
  * ✅  基础类型
  * ❎  泛型
* ❎  用例