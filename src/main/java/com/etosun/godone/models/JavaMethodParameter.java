/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-07 下午7:40
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 方法参数模型
 */
@Data
public class JavaMethodParameter implements Serializable {
    // 参数名称
    @JSONField(ordinal = 0)
    String name;

    // 参数类型
    @JSONField(ordinal = 1)
    JavaActualType type;

    // 注释
    @JSONField(ordinal = 2)
    JavaDescriptionModel description;

    // 参数注解
    @JSONField(ordinal = 3)
    List<JavaAnnotationModel> annotations;
}
