/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.ClsField;
import com.etosun.godone.models.JavaCls;
import com.etosun.godone.util.ClsUtil;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.ArrayList;

public class ClassAnalysis {
    private final JavaClass currentClass;

    public ClassAnalysis(JavaClass javaClass) {
        currentClass = javaClass;
    }

    /**
     * 解析 class 中的字段
     */
    private ArrayList<ClsField> getField() {
        ArrayList<ClsField> fields = new ArrayList<>();

        currentClass.getFields().forEach(f -> {
            ClsField field = new ClsField();

            field.setName(f.getName());
            field.setType(ClsUtil.getType(f.getType(), currentClass.getSource().getImports()));
            field.setDescription(ClsUtil.getDescription(f.getComment(), f.getTags()));
            field.setAnnotation(ClsUtil.getAnnotation(f.getAnnotations(), currentClass.getSource().getImports()));

            fields.add(field);
        });

        return fields;
    }

    public JavaCls run() {
        JavaCls javaCls = new JavaCls();

        javaCls.setName(currentClass.getName());
        javaCls.setClassPath(String.format("%s.%s", currentClass.getPackageName(), currentClass.getName()));
        // 注释
        javaCls.setDescription(ClsUtil.getDescription(currentClass.getComment(), currentClass.getTags()));
        // 注解
        javaCls.setAnnotation(ClsUtil.getAnnotation(currentClass.getAnnotations(), currentClass.getSource().getImports()));

        javaCls.setIsEnum(currentClass.isEnum());
        javaCls.setIsPublic(currentClass.isPublic());
        javaCls.setIsPrivate(currentClass.isPrivate());
        javaCls.setIsAbstract(currentClass.isAbstract());
        javaCls.setIsInterface(currentClass.isInterface());

        // 字段
        javaCls.setFields(getField());
        // 方法
        currentClass.getMethods().forEach(m -> {
            javaCls.getMethods().add(new MethodAnalysis(currentClass, m).run());
        });

        return javaCls;
    }
}
