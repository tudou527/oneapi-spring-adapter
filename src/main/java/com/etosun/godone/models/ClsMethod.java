/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-02 下午11:57
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;

/**
 * class 方法模型
 */
@Data
public class ClsMethod {
    // 方法名
    @JSONField(ordinal = 0)
    String name;

    // 注释
    @JSONField(ordinal = 1)
    Description description;

    // 方法注解
    @JSONField(ordinal = 3)
    ArrayList<Annotation> annotation = new ArrayList<>();

    // 入参
    @JSONField(ordinal = 4)
    ArrayList<MethodParameter> parameters = new ArrayList<>();

    // 返回值
    @JSONField(ordinal = 5)
    JavaActualType returns;
}
