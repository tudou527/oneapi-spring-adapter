/**
 *
 * @auther xiaoyun
 * @create 2021-11-22 下午8:12
 */
package com.oneapi.spring.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class JavaAnnotationField implements Serializable {
    // 字段名称
    @JSONField(ordinal = 10)
    String name;
    
    /**
     * 字段类型
     * 考虑到 java 为强类型，不可能出现字段值为数组但每个数组项目类型都不同的情况
     * 这里认为属性每个值的类型都一样
     */
    @JSONField(ordinal = 20)
    String type;
    
    // 属性值是否为数组
    @JSONField(ordinal = 30)
    boolean isArray;
    
    // 字段值
    @JSONField(ordinal = 40)
    Object value;
}
