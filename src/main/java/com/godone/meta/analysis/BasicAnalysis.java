/**
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.godone.meta.analysis;

import com.godone.meta.cache.ReflectCache;
import com.godone.meta.cache.ResourceCache;
import com.godone.meta.models.*;
import com.godone.meta.utils.ClassUtil;
import com.godone.meta.utils.FileUtil;
import com.godone.meta.utils.MavenUtil;
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
    JavaFileModel fileModel;
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
        JavaFileModel model = analysisFromResource(classPath);

        if (model != null) {
            return model;
        }
    
        // 判断 classPath 是否来自 JAR 包
        return analysisFromReflect(classPath);
    }
    
    // 从本项目的资源文件中获得解析结果
    private JavaFileModel analysisFromResource(String classPath) {
        String childClassName = "";
        String javaFilePath = resourceCache.getCache(classPath);
        
        if (javaFilePath == null) {
            return null;
        }
    
        // 兼容子类的情况
        if (classPath.contains("$")) {
            childClassName = classPath.split("\\$")[1];
            javaFilePath = resourceCache.getCache(classPath.split("\\$")[0]);
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
        // 处理子类
        if (!childClassName.isEmpty()) {
            String finalChildClassName = childClassName;
            optionalClass = builder.getClasses().stream().filter(cls -> cls.getName().equals(finalChildClassName)).findFirst();
        }
        
        if (!optionalClass.isPresent()) {
            return null;
        }

        fileModel = new JavaFileModel();;
        targetClass = optionalClass.get();

        fileModel.setFilePath(javaFilePath);
        fileModel.setImports(classUtil.getImports(targetClass));
        fileModel.setPackageName(targetClass.getPackageName());
        
        // 暂存 source 用于后续解析类型
        fileModel.setJavaSource(targetClass.getSource());

        // 文件注释
        JavaDescriptionModel description = classUtil.getDescription(targetClass, fileLines);
        fileModel.setDescription(description);
    
        JavaClassModel classModel = analysisClass(targetClass);
        // 需要特殊处理子类，避免覆盖父类解析结果
        if (!childClassName.isEmpty()) {
            classModel.setClassPath(classPath);
        }
        fileModel.setClassModel(classModel);

        return fileModel;
    }
    
    // 从反编译的 jar 包中获取解析结果
    private JavaFileModel analysisFromReflect(String classPath) {
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
            fileUtil.exec(String.join(" ", new ArrayList<String>(){{
                add("java");
                add("-jar");
                add(fileUtil.getCurrentDir() +"/lib/procyon-decompiler.jar");
                add("-jar");
                add(jarFilePath);
                add("-o");
                add(deCompileFile.getPath());
            }}), jarFile.getParent());
            
            // 把编反编译结果作为资源缓存起来
            mvnUtil.saveResource(deCompileFile.getPath(), false);
        }

        return analysisFromResource(resourceCache.getCache(classPath));
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
        classModel.setAnnotations(classUtil.getAnnotation(javaClass.getAnnotations(), fileModel));

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
            field.setAnnotations(classUtil.getAnnotation(f.getAnnotations(), fileModel));
        
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
