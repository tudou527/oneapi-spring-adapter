/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午7:03
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java 类型
 */
@Data
public class JavaActualType implements Serializable {
    // 类型字面量
    @JSONField(ordinal = 10)
    String name;
    
    // class 路径 a.b.c.d
    @JSONField(ordinal = 15)
    String classPath;

    // 子类型
    @JSONField(ordinal = 20)
    ArrayList<JavaActualType> item;
    
    @Override
    public String toString() {
        return this.name;
    }
}
