package com.etosun.godone.analysis;

import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.cache.ResourceCache;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaClassFieldModel;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * JAR 包资源解析
 */
@Slf4j
public class ReflectAnalysis {
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
    
    @Inject
    private ResourceCache resourceCache;
    @Inject
    private PendingCache pendingCache;
    @Inject
    private ReflectCache reflectCache;
    
    @Inject
    private Provider<BasicAnalysis> basicAnalysis;
    
    public JavaFileModel analysis(String classPath) {
        // 判断 classPath 是否来自 JAR 包
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
    
            String resourceFilePath = resourceCache.getCache(classPath);
            if (resourceFilePath != null) {
                return basicAnalysis.get().analysis(resourceFilePath);
            }
        }
        
        // 从 JAR 包中构建解析结果
        return builderFromReflectClass(classPath, jarFilePath);
    }
    
    // 从 JAR 包中构建解析结果
    private JavaFileModel builderFromReflectClass(String classPath, String jarFilePath) {
        JavaFileModel fileModel = new JavaFileModel();
        
        // 使用 jar 包地址
        fileModel.setFilePath(jarFilePath);
        // import 地址为空
        fileModel.setImports(new ArrayList<>());
        fileModel.setPackageName(classPath);
        
        String simpleClassName = classPath.substring(classPath.lastIndexOf(".") + 1);

        Class<?> targetClass = mvnUtil.getMatchReflectClass(classPath, jarFilePath);
        if (targetClass != null) {
            JavaClassModel classModel = new JavaClassModel();
            classModel.setName(simpleClassName);
            classModel.setClassPath(classPath);
            
            // 父类
            classModel.setSuperClass(getParentClass(targetClass));
            
            // 字段
            ArrayList<JavaClassFieldModel> fieldList = new ArrayList<>();
            mvnUtil.getFieldList(targetClass).forEach(f -> {
                JavaClassFieldModel field = new JavaClassFieldModel();
        
                log.info("  analysis field: {}", f.getName());
        
                field.setName(f.getName());
                // 字段类型
                field.setType(getReflectType(f, fileModel));
                fieldList.add(field);
            });
            classModel.setFields(fieldList);
    
            fileModel.setClassModel(classModel);
        }
        
        return fileModel;
    }
    
    // 解析反射获得的字段类型
    public JavaActualType getReflectType(Field field, JavaFileModel fileModel) {
        JavaActualType javaType = new JavaActualType();

        String fullTypeName = field.getType().getTypeName();
        String simpleTypeName = fullTypeName.substring(fullTypeName.lastIndexOf(".") + 1);
        
        if (simpleTypeName.contains("$")) {
            simpleTypeName = simpleTypeName.split("\\$")[1];
        }
    
        // 取 className 作为类型名称
        javaType.setName(simpleTypeName);
    
        if (TypeAnalysis.startsWithBlackList.stream().anyMatch(fullTypeName::startsWith) || fullTypeName.length() == 1) {
            javaType.setClassPath(fullTypeName);
        } else {
            System.out.printf(">>>>>> %s", field.toString());
        }
        
        return javaType;
    }
    
    // 父类
    private JavaActualType getParentClass(Class<?> javaClass) {
        Class<?> superClass = javaClass.getSuperclass();

        if (superClass == null) {
            return null;
        }
        
        String superClassName = superClass.getName();
        String simpleSuperClassName = superClassName.substring(superClassName.lastIndexOf(".") + 1);

        if (BasicAnalysis.superClassBlackList.contains(simpleSuperClassName)) {
            return null;
        }
    
        JavaActualType actualType = new JavaActualType();
        actualType.setName(simpleSuperClassName);
        actualType.setClassPath(superClassName);
    
        if (TypeAnalysis.startsWithBlackList.stream().noneMatch(simpleSuperClassName::startsWith) && simpleSuperClassName.length() != 1) {
            pendingCache.setCache(superClassName);
        }
        
        return actualType;
    }
}
