/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午7:03
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;

/**
 * Java 类型
 *
 */
@Data
public class JavaActualType {
    // 类型字面量
    @JSONField(ordinal = 1)
    String name;

    // 子类型
    @JSONField(ordinal = 2)
    ArrayList<JavaActualType> item;
}
