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
import com.google.inject.Provider;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 资源解析
 */
@Slf4j
public class BasicAnalysis {
    // 当前解析的 .java 文件
    String javaFilePath;
    // 按行读取的文件内容
    List<String> fileLines = new ArrayList<>();
    // 解析结果
    JavaFileModel fileModel = new JavaFileModel();
    // 被解析的 class
    JavaClass targetClass;

    @Inject
    FileUtil fileUtil;
    @Inject
    ClassUtil classUtil;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;
    
    public static List<String> superClassBlackList = new ArrayList<String>(){{
        add("Object");
    }};
    
    public JavaFileModel analysis(String filePath) {
        javaFilePath = filePath;

        JavaProjectBuilder builder = fileUtil.getBuilder(javaFilePath);
        if (builder == null) {
            return fileModel;
        }

        try {
            // 尝试按行读取文件内容，用于处理单行文件注释
            fileLines = Files.readAllLines(Paths.get(javaFilePath), fileUtil.getFileOrIOEncode(javaFilePath));
        } catch (IOException ignored) {}

        Optional<JavaClass> optionalClass = builder.getClasses().stream().filter(JavaClass::isPublic).findFirst();
        if (!optionalClass.isPresent()) {
            return fileModel;
        }
        targetClass = optionalClass.get();

        fileModel.setFilePath(javaFilePath);
        fileModel.setImports(classUtil.getImports(targetClass));
        fileModel.setPackageName(targetClass.getPackageName());

        // 文件注释
        JavaDescriptionModel description = classUtil.getDescription(targetClass, fileLines);
        fileModel.setDescription(description);

        fileModel.setClassModel(analysisClass(targetClass));

        return fileModel;
    }
    
    // 分析 class
    private JavaClassModel analysisClass(JavaClass javaClass) {
        JavaClassModel classModel = new JavaClassModel();

        classModel.setName(javaClass.getName());
        classModel.setClassPath(String.format("%s.%s", javaClass.getPackageName(), javaClass.getName()));
        classModel.setActualType(classUtil.getActualTypeParameters(javaClass));
        
        // 继承关系
        classModel.setSuperClass(getParentClass(javaClass));
        
        // 描述&注解
        classModel.setDescription(classUtil.getDescription(javaClass, fileLines));
        classModel.setAnnotation(classUtil.getAnnotation(javaClass.getAnnotations(), fileModel));

        classModel.setIsEnum(javaClass.isEnum());
        classModel.setIsPublic(javaClass.isPublic());
        classModel.setIsPrivate(javaClass.isPrivate());
        classModel.setIsAbstract(javaClass.isAbstract());
        classModel.setIsInterface(javaClass.isInterface());
    
        log.info("analysis class: {}", javaClass.getName());

        // class 字段
        ArrayList<JavaClassFieldModel> fieldList = new ArrayList<>();
        javaClass.getFields().forEach(f -> {
            JavaClassFieldModel field = new JavaClassFieldModel();
    
            log.info("  analysis field: {}", f.getName());
    
            field.setName(f.getName());
            field.setDefaultValue(f.getInitializationExpression());

            field.setIsPublic(f.isPublic());
            field.setIsPrivate(f.isPrivate());
            field.setIsProtected(f.isProtected());
            // 字段类型
            field.setType(typeAnalysis.get().analysis(f.getType(), fileModel));
            // 描述&注解
            field.setDescription(classUtil.getDescription(f, fileLines));
            field.setAnnotation(classUtil.getAnnotation(f.getAnnotations(), fileModel));
    
            fieldList.add(field);
        });
        classModel.setFields(fieldList);

        return classModel;
    }
    
    // 父类
    private JavaActualType getParentClass(JavaClass javaClass) {
        JavaType superClass = javaClass.getSuperClass();
        
        if (superClass == null) {
            return null;
        }
    
        String simpleSuperClassName = superClass.getBinaryName().substring(superClass.getBinaryName().lastIndexOf(".") + 1);
        if (superClassBlackList.contains(simpleSuperClassName)) {
            return null;
        }
        return typeAnalysis.get().analysis(superClass, fileModel);
    }
}
