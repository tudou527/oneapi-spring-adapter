/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.etosun.godone.analysis;

import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.cache.ResourceCache;
import com.etosun.godone.models.*;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
    // 按行读取的文件内容
    List<String> fileLines = new ArrayList<>();
    // 解析结果
    JavaFileModel fileModel = new JavaFileModel();
    // 被解析的 class
    JavaClass targetClass;

    @Inject
    FileUtil fileUtil;
    @Inject
    MavenUtil mvnUtil;
    @Inject
    ClassUtil classUtil;
    @Inject
    ResourceCache resourceCache;
    @Inject
    private ReflectCache reflectCache;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;
    
    public static List<String> superClassBlackList = new ArrayList<String>(){{
        add("Object");
    }};
    
    public JavaFileModel analysis(String classPath) {
        fileModel = getModelFromResource(resourceCache.getCache(classPath));

        if (fileModel != null) {
            return fileModel;
        }
    
        // 判断 classPath 是否来自 JAR 包
        return getModelFromReflect(classPath);
    }
    
    // 从本项目的资源文件中获得解析结果
    public JavaFileModel getModelFromResource(String javaFilePath) {
        if (javaFilePath == null) {
            return null;
        }

        JavaProjectBuilder builder = fileUtil.getBuilder(javaFilePath);
        if (builder == null) {
            return null;
        }

        try {
            // 尝试按行读取文件内容，用于处理单行文件注释
            fileLines = Files.readAllLines(Paths.get(javaFilePath), fileUtil.getFileOrIOEncode(javaFilePath));
        } catch (IOException ignored) {}

        Optional<JavaClass> optionalClass = builder.getClasses().stream().filter(JavaClass::isPublic).findFirst();
        if (!optionalClass.isPresent()) {
            return null;
        }
        targetClass = optionalClass.get();

        fileModel.setFilePath(javaFilePath);
        fileModel.setImports(classUtil.getImports(targetClass));
        fileModel.setPackageName(targetClass.getPackageName());
        
        // 暂存 source 用于后续解析类型
        fileModel.setJavaSource(targetClass.getSource());

        // 文件注释
        JavaDescriptionModel description = classUtil.getDescription(targetClass, fileLines);
        fileModel.setDescription(description);

        fileModel.setClassModel(analysisClass(targetClass));

        return fileModel;
    }
    
    // 从反编译的 jar 包中获取解析结果
    public JavaFileModel getModelFromReflect(String classPath) {
        String jarFilePath = reflectCache.getCache(classPath);
        if (jarFilePath == null) {
            return null;
        }
        
        // 判断源码 JAR 是否存在
        File sourceJar = new File(jarFilePath.replace(".jar", "-sources.jar"));
        if (sourceJar.exists()) {
            String zipDir = sourceJar.getParent() + "/source";
            // 解压源码JAR
            fileUtil.unzipJar(sourceJar, zipDir);
            // 缓存解压的资源文件
            mvnUtil.saveResource(zipDir, false);
        } else {
            File jarFile = new File(jarFilePath);
            // 反编译后的文件保存目录
            File deCompileFile = new File(jarFile.getParent() + "/deCompile");
            
            if (deCompileFile.exists()) {
                deCompileFile.delete();
            }

            // 反编译 jar 包
            fileUtil.exec(new String[]{
                "java",
                "-jar",
                "/Users/xiaoyun/github/godone/src/main/resources/lib/procyon-decompiler.jar",
                "-jar",
                jarFilePath,
                "-o",
                deCompileFile.getPath(),
            }, jarFile.getParent());
            
            // 把编反编译结果作为资源缓存起来
            mvnUtil.saveResource(deCompileFile.getPath(), false);
        }
    
        return getModelFromResource(resourceCache.getCache(classPath));
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

        classModel.setFields(getFieldList(javaClass));

        return classModel;
    }
    
    // 字段列表
    private ArrayList<JavaClassFieldModel> getFieldList(JavaClass javaClass) {
        ArrayList<JavaClassFieldModel> fieldList = new ArrayList<>();
    
        // 过滤掉 static 字段
        javaClass.getFields().stream().filter(f -> !f.isStatic()).forEach(f -> {
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

        return fieldList;
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
