/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.*;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMember;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析 java 文件中的 class
 */
public class EntryAnalysis extends BasicAnalysis {

    // 入口函数
    public JavaFileModel analysis(String filePath) {
        super.analysis(filePath);
    
        fileModel.setFileType(ClassTypeEnum.ENTRY);
        // 补全方法（只考虑配 public 方法）
        targetClass.getMethods().forEach(method -> {
            fileModel.getClassModel().getMethods().add(analysisMethod(method));
        });

        return fileModel;
    }

    // 解析方法
    private JavaClassMethodModel analysisMethod(JavaMethod method) {
        JavaClassMethodModel javaMethod = new JavaClassMethodModel();

        javaMethod.setName(method.getName());
        // 描述&注解
        javaMethod.setDescription(classUtil.getDescription(method, fileLines));
        javaMethod.setAnnotation(classUtil.getAnnotation(method.getAnnotations(), fileModel));

        // 入参及类型
        javaMethod.setParameters(getParameters(method));
    
        logger.message("  analysis method: %s", method.getName());

        // 返回值及类型
        JavaActualType methodReturnType = typeAnalysis.get().analysis(method.getReturnType(), fileModel);
        javaMethod.setReturns(methodReturnType);

        return javaMethod;
    }

    // 方法入参
    private ArrayList<JavaMethodParameter> getParameters(JavaMethod method) {
        ArrayList<JavaMethodParameter> paramList = new ArrayList<>();

        List<JavaParameter> parameters = method.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return paramList;
        }

        parameters.forEach(p -> {
            JavaMethodParameter param = new JavaMethodParameter();

            param.setName(p.getName());
        
            // 方法入参及类型
            JavaActualType paramType = typeAnalysis.get().analysis(p.getType(), fileModel);
            param.setType(paramType);

            // 参数描述及注解
            param.setDescription(classUtil.getDescription(p, fileLines));
            param.setAnnotations(classUtil.getAnnotation(p.getAnnotations(), fileModel));

            paramList.add(param);
        });

        return paramList;
    }
}
