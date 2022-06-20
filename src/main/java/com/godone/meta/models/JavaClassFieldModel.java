/**
 *
 * @auther xiaoyun
 * @create 2021-02-02 下午11:50
 */
package com.godone.meta.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * class 字段
 */
@Data
public class JavaClassFieldModel implements Serializable {
    // 字段名称
    @JSONField(ordinal = 0)
    String name;

    // 字段类型
    @JSONField(ordinal = 10)
    JavaActualType type;

    // 字段默认值
    @JSONField(ordinal = 20)
    String defaultValue;
    
    // 是否 private
    @JSONField(ordinal = 21)
    Boolean isPrivate = false;
    
    // 是否 private
    @JSONField(ordinal = 22)
    Boolean isPublic = true;
    
    // 是否 protected
    @JSONField(ordinal = 23)
    Boolean isProtected = false;

    // 注释
    @JSONField(ordinal = 30)
    JavaDescriptionModel description;

    // 字段注解
    @JSONField(ordinal = 40)
    ArrayList<JavaAnnotationModel> annotations = new ArrayList();
}
