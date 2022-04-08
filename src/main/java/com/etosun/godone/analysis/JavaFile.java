/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.*;
import com.etosun.godone.util.ClassUtil;
import com.etosun.godone.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 解析 java 文件中的 class
 */
public class JavaFile {
    String filePath;
    List<String> fileLines;
    JavaFileModel fileModel;
    JavaSource fileSource;
    JavaProjectBuilder builder;

    @SneakyThrows
    public JavaFile(String filePath) {
        builder = FileUtil.getBuilder(filePath);
        fileLines = Files.readAllLines(Paths.get(filePath), FileUtil.getFileOrIOEncode(filePath));
    }

    // 入口函数
    public JavaFileModel analysis() {
        if (builder == null) {
            return null;
        }

        Optional<JavaSource> javaSource = builder.getSources().stream().findFirst();
        if (!javaSource.isPresent()) {
            return null;
        }

        fileSource = javaSource.get();

        fileModel = new JavaFileModel();
        fileModel.setFilePath(filePath);
        fileModel.setImports(fileSource.getImports());

        JavaPackage pkg = fileSource.getPackage();

        // 文件注释
        fileModel.setPackageName(pkg.getName());
        JavaDescriptionModel description = ClassUtil.getDescription(pkg, fileLines);
        fileModel.setDescription(description);

        // 解析 class
        fileSource.getClasses().forEach(cls -> {
            fileModel.getClassList().add(analysisClass(cls));
        });

        return fileModel;
    }

    private JavaClassModel analysisClass(JavaClass javaClass) {
        JavaClassModel classModel = new JavaClassModel();

        classModel.setName(javaClass.getName());
        classModel.setClassPath(String.format("%s.%s", javaClass.getPackageName(), javaClass.getName()));
        classModel.setType(ClassUtil.getClassTypeParameters(javaClass));
        // 注释
        classModel.setDescription(ClassUtil.getDescription(javaClass, fileLines));
        // 注解
        classModel.setAnnotation(ClassUtil.getAnnotation(javaClass.getAnnotations(), fileModel.getImports()));

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
        javaMethod.setDescription(ClassUtil.getDescription(method, fileLines));
        // 注解
        javaMethod.setAnnotation(ClassUtil.getAnnotation(method.getAnnotations(), fileModel.getImports()));

        // 入参
        javaMethod.setParameters(getParameters(method, javaClass));
        // 返回值
        javaMethod.setReturns(ClassUtil.getType(method.getReturnType(), fileModel.getImports()));

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
            field.setType(ClassUtil.getType(f.getType(), fileModel.getImports()));
            field.setDescription(ClassUtil.getDescription(f, fileLines));
            field.setAnnotation(ClassUtil.getAnnotation(f.getAnnotations(), fileModel.getImports()));

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
            param.setType(ClassUtil.getType(p.getType(), fileModel.getImports()));
            param.setDescription(ClassUtil.getDescription(p, fileLines));
            param.setAnnotations(ClassUtil.getAnnotation(p.getAnnotations(), fileModel.getImports()));

            paramList.add(param);
        });

        return paramList;
    }
}
