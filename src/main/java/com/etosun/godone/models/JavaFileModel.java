/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-19 下午7:54
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.qdox.model.JavaSource;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class JavaFileModel implements Serializable {
    // 文件路径
    @JSONField(ordinal = 0)
    String filePath;
    
    // 资源类型
    @JSONField(ordinal = 1)
    ClassTypeEnum fileType = ClassTypeEnum.RESOURCE;

    // 包名
    @JSONField(ordinal = 10)
    String packageName;

    // 文件
    @JSONField(ordinal = 20)
    JavaDescriptionModel description;

    // import 列表
    @JSONField(ordinal = 30)
    List<String> imports = new LinkedList<>();
    
    // class 列表
    @JSONField(ordinal = 40)
    JavaClassModel classModel;
    
    // 暂存 source 用于执行过程中解析类型（缓存时候会被删掉）
    JavaSource javaSource;
}
