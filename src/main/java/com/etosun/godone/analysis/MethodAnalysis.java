/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午9:14
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.ClsMethod;
import com.etosun.godone.models.JavaFile;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.MethodParameter;
import com.etosun.godone.util.ClsUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.ArrayList;
import java.util.List;

public class MethodAnalysis {
    // 当前解析的 class
    private final JavaClass currentClass;
    // 当前解析的方法
    private final JavaMethod currentMethod;

    public MethodAnalysis(JavaClass javaClass, JavaMethod method) {
        currentClass = javaClass;
        currentMethod = method;
    }

    /**
     * 方法入参
     */
    private ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> paramList = new ArrayList<>();

        List<JavaParameter> parameters = currentMethod.getParameters();

        if (parameters == null || parameters.isEmpty()) {
            return paramList;
        }

        parameters.forEach(p -> {
            MethodParameter param = new MethodParameter();

            param.setName(p.getName());
            param.setType(ClsUtil.getType(p.getType(), currentClass.getSource().getImports()));
            param.setDescription(ClsUtil.getDescription(p.getComment(), p.getTags()));
            param.setAnnotations(ClsUtil.getAnnotation(p.getAnnotations(), currentClass.getSource().getImports()));

            paramList.add(param);
        });

        return paramList;
    }

    /**
     * 方法返回值
     */
    private JavaActualType getReturn() {
        JavaActualType type = new JavaActualType();
        JavaClass cls = currentMethod.getReturns();

        type.setName(cls.getName());

        return type;
    }

    public ClsMethod run() {
        ClsMethod javaMethod = new ClsMethod();

        javaMethod.setName(currentMethod.getName());
        // 注释
        javaMethod.setDescription(ClsUtil.getDescription(currentMethod.getComment(), currentMethod.getTags()));
        // 注解
        javaMethod.setAnnotation(ClsUtil.getAnnotation(currentMethod.getAnnotations(), currentClass.getSource().getImports()));

        // 入参
        javaMethod.setParameters(getParameters());
        // 返回值
        javaMethod.setReturns(ClsUtil.getType(currentMethod.getReturnType(), currentClass.getSource().getImports()));

        return javaMethod;
    }
}
