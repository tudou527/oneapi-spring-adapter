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
public class JavaClassModel {
    // 类名
    @JSONField(ordinal = 0)
    String name;

    // class 路径 a.b.c.d
    @JSONField(ordinal = 10)
    String classPath;

    // 联合类型
    @JSONField(ordinal = 11)
    ArrayList<JavaActualType> type;

    // 注释
    @JSONField(ordinal = 20)
    JavaDescriptionModel description;

    // 注解
    @JSONField(ordinal = 30)
    ArrayList<JavaAnnotationModel> annotation = new ArrayList<>();

    // 是否枚举
    @JSONField(ordinal = 40)
    Boolean isEnum = false;

    // 是否 interface
    @JSONField(ordinal = 50)
    Boolean isInterface = false;

    // 是否 抽象类
    @JSONField(ordinal = 60)
    Boolean isAbstract = false;

    // 是否 private class
    @JSONField(ordinal = 70)
    Boolean isPrivate = false;

    // 是否 private class
    @JSONField(ordinal = 80)
    Boolean isPublic = false;

    // 属性
    @JSONField(ordinal = 90)
    ArrayList<JavaClassFieldModel> fields = new ArrayList<>();

    // 方法
    @JSONField(ordinal = 100)
    ArrayList<JavaClassMethodModel> methods = new ArrayList<>();
}
