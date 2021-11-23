/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-11-22 下午8:12
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;

@Data
public class JavaAnnotationModel {
    // 注解名
    @JSONField(ordinal = 0)
    String name;

    // 注解 class
    @JSONField(ordinal = 1)
    String classPath;

    // 注解字段
    @JSONField(ordinal = 2)
    HashMap<String, Object> fields = new HashMap<>();
}
