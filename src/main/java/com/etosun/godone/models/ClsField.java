/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-02 下午11:50
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;

/**
 * class 字段
 */
@Data
public class ClsField {
    // 字段名称
    @JSONField(ordinal = 0)
    String name;

    // 字段类型
    @JSONField(ordinal = 1)
    JavaActualType type;

    // 字段默认值
    @JSONField(ordinal = 2)
    String defaultValue;

    // 注释
    @JSONField(ordinal = 3)
    Description description;

    // 字段注解
    @JSONField(ordinal = 4)
    ArrayList<Annotation> annotation = new ArrayList<>();
}
