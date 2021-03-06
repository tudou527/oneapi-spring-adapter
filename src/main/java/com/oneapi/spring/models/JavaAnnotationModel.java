/**
 *
 * @auther xiaoyun
 * @create 2021-11-22 下午8:12
 */
package com.oneapi.spring.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class JavaAnnotationModel implements Serializable {
    // 注解名
    @JSONField(ordinal = 0)
    String name;

    // 注解 class
    @JSONField(ordinal = 1)
    String classPath;

    // 注解字段
    @JSONField(ordinal = 2)
    ArrayList<JavaAnnotationField> fields = new ArrayList();
}
