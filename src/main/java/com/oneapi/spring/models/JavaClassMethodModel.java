/**
 *
 * @auther xiaoyun
 * @create 2021-02-02 下午11:57
 */
package com.oneapi.spring.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * class 方法模型
 */
@Data
public class JavaClassMethodModel implements Serializable {
    // 方法名
    @JSONField(ordinal = 0)
    String name;

    // 注释
    @JSONField(ordinal = 1)
    JavaDescriptionModel description;

    // 方法注解
    @JSONField(ordinal = 3)
    ArrayList<JavaAnnotationModel> annotations = new ArrayList<>();

    // 入参
    @JSONField(ordinal = 4)
    ArrayList<JavaMethodParameter> parameters = new ArrayList<>();

    // 返回值
    @JSONField(ordinal = 5, name="return")
    JavaActualType returnType;
}
