# OneAPI Spring Adapter

![ci](https://github.com/tudou527/oneapi-spring-adapter/actions/workflows/workflow.yml/badge.svg)
[![codecov](https://codecov.io/gh/tudou527/oneapi-spring-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/tudou527/oneapi-spring-adapter)

## 介绍
这是一个 OneAPI 适配 Spring 规范的 HTTP 生产工具，帮助前端完善 API 工程化能力。

## 为什么会有这个项目
有 Swagger 珠玉在前，业界主要做的是 API 消费相关产品（如 API 管理平台)。Swagger 的原理是运行时从 Spring 上下文中拿到所有路由信息。由于依赖后端接入 Swagger，导致 API 生产/消费链路前端完全不可控。

## OneAPI 的方案
`OneAPI` 的实现方案是静态解析（类似 AST），扫描项目下所有的 .java 文件，根据规范（本项目遵循的是 Spring 规范）识别 API 定义与相关的类型文件（用于 TS 场景），生成 JSON Schema 供前端消费。

基于 `OneAPI` 的静态解析，可以通过诸如 GitLab Hooks 便捷的实现 API 生产自动化：

<img src="https://raw.githubusercontent.com/tudou527/oneapi-spring-adapter/master/images/follow.png" width="760" />

## 使用方式

