/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-19 下午8:20
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;

/**
 * 注解模型
 */
@Data
public class Annotation {
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
