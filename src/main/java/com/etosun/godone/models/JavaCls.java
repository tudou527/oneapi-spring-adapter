/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-19 下午8:00
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;

/**
 * Class 模型
 */
@Data
public class JavaCls {
    // 类名
    @JSONField(ordinal = 0)
    String name;

    // class 路径 a.b.c.d
    @JSONField(ordinal = 1)
    String classPath;

    // 注释
    @JSONField(ordinal = 2)
    Description description;

    // 注解
    @JSONField(ordinal = 4)
    ArrayList<Annotation> annotation = new ArrayList<Annotation>();

    // 是否枚举
    @JSONField(ordinal = 5)
    Boolean isEnum = false;

    // 是否 interface
    @JSONField(ordinal = 6)
    Boolean isInterface = false;

    // 是否 抽象类
    @JSONField(ordinal = 7)
    Boolean isAbstract = false;

    // 是否 private class
    @JSONField(ordinal = 8)
    Boolean isPrivate = false;

    // 是否 private class
    @JSONField(ordinal = 9)
    Boolean isPublic = false;

    // 属性
    @JSONField(ordinal = 10)
    ArrayList<ClsField> fields = new ArrayList<>();

    // 方法
    @JSONField(ordinal = 11)
    ArrayList<ClsMethod> methods = new ArrayList<>();
}
