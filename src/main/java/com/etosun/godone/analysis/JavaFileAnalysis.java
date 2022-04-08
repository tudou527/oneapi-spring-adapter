/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.*;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.google.inject.Inject;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 解析 java 文件中的 class
 */
public class JavaFileAnalysis {
    // 文件路径
    String filePath;
    // 按行读取的文件内容
    List<String> fileLines;
    // 解析结果
    JavaFileModel fileModel;

    @Inject
    private FileUtil fileUtil;
    @Inject
    private ClassUtil classUtil;

    // 入口函数
    public JavaFileModel analysis(String filePath) {
        JavaProjectBuilder builder = fileUtil.getBuilder(filePath);

        if (builder == null) {
            return null;
        }

        Optional<JavaSource> OptionalJavaSource = builder.getSources().stream().findFirst();
        if (!OptionalJavaSource.isPresent()) {
            return null;
        }

        JavaSource javaSource = OptionalJavaSource.get();
        try {
            fileLines = Files.readAllLines(Paths.get(filePath), fileUtil.getFileOrIOEncode(filePath));
        } catch (IOException ignored) {
        }

        fileModel = new JavaFileModel();
        fileModel.setFilePath(filePath);
        fileModel.setImports(javaSource.getImports());

        JavaPackage pkg = javaSource.getPackage();

        // 文件注释
        fileModel.setPackageName(pkg.getName());
        JavaDescriptionModel description = classUtil.getDescription(pkg, fileLines);
        fileModel.setDescription(description);

        // 解析 class
        javaSource.getClasses().forEach(cls -> {
            fileModel.getClassList().add(analysisClass(cls));
        });

        return fileModel;
    }

    private JavaClassModel analysisClass(JavaClass javaClass) {
        JavaClassModel classModel = new JavaClassModel();

        classModel.setName(javaClass.getName());
        classModel.setClassPath(String.format("%s.%s", javaClass.getPackageName(), javaClass.getName()));
        classModel.setType(classUtil.getClassTypeParameters(javaClass));
        // 注释
        classModel.setDescription(classUtil.getDescription(javaClass, fileLines));
        // 注解
        classModel.setAnnotation(classUtil.getAnnotation(javaClass.getAnnotations(), fileModel.getImports()));

        classModel.setIsEnum(javaClass.isEnum());
        classModel.setIsPublic(javaClass.isPublic());
        classModel.setIsPrivate(javaClass.isPrivate());
        classModel.setIsAbstract(javaClass.isAbstract());
        classModel.setIsInterface(javaClass.isInterface());

        // 字段
        classModel.setFields(getFields(javaClass));
        // 方法
        javaClass.getMethods().forEach(method -> {
            classModel.getMethods().add(analysisMethod(method, javaClass));
        });

        return classModel;
    }

    /**
     * 解析方法
     */
    private JavaClassMethodModel analysisMethod(JavaMethod method, JavaClass javaClass) {
        JavaClassMethodModel javaMethod = new JavaClassMethodModel();

        javaMethod.setName(method.getName());
        // 注释
        javaMethod.setDescription(classUtil.getDescription(method, fileLines));
        // 注解
        javaMethod.setAnnotation(classUtil.getAnnotation(method.getAnnotations(), fileModel.getImports()));

        // 入参
        javaMethod.setParameters(getParameters(method, javaClass));
        // 返回值
        javaMethod.setReturns(classUtil.getType(method.getReturnType(), fileModel.getImports()));

        return javaMethod;
    }

    /**
     * 解析 class 中的字段
     */
    private ArrayList<JavaClassFieldModel> getFields(JavaClass javaClass) {
        ArrayList<JavaClassFieldModel> fields = new ArrayList<>();

        javaClass.getFields().forEach(f -> {
            JavaClassFieldModel field = new JavaClassFieldModel();

            field.setName(f.getName());
            field.setDefaultValue(f.getInitializationExpression());
            field.setType(classUtil.getType(f.getType(), fileModel.getImports()));
            field.setDescription(classUtil.getDescription(f, fileLines));
            field.setAnnotation(classUtil.getAnnotation(f.getAnnotations(), fileModel.getImports()));

            fields.add(field);
        });

        return fields;
    }

    /**
     * 方法入参
     */
    private ArrayList<JavaMethodParameter> getParameters(JavaMethod method, JavaClass javaClass) {
        ArrayList<JavaMethodParameter> paramList = new ArrayList<>();

        List<JavaParameter> parameters = method.getParameters();

        if (parameters == null || parameters.isEmpty()) {
            return paramList;
        }

        parameters.forEach(p -> {
            JavaMethodParameter param = new JavaMethodParameter();

            param.setName(p.getName());
            param.setType(classUtil.getType(p.getType(), fileModel.getImports()));
            param.setDescription(classUtil.getDescription(p, fileLines));
            param.setAnnotations(classUtil.getAnnotation(p.getAnnotations(), fileModel.getImports()));

            paramList.add(param);
        });

        return paramList;
    }
}
