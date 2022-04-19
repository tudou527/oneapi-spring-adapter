package com.etosun.godone.analysis;

import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.*;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.File;
import java.util.ArrayList;

/**
 * JAR 包资源解析
 */
public class ReflectAnalysis {
    @Inject
    private Logger logger;
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private ClassUtil classUtil;
    @Inject
    private FileUtil fileUtil;

    @Inject
    private CommonCache commonCache;
    @Inject
    private Provider<BasicAnalysis> basicAnalysis;
    @Inject
    private Provider<TypeAnalysis> typeAnalysis;
    

    public JavaFileModel analysis(String classPath) {
        // 判断 classPath 是否来自 JAR 包
        String jarFilePath = commonCache.getReflectClass().get(classPath);
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
    
            String resourceFilePath = commonCache.getResource(classPath);
            if (resourceFilePath != null) {
                return basicAnalysis.get().analysis(resourceFilePath);
            }
        }
        
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
            
            // TODO: 处理字段
        }
        
        return fileModel;
    }
    
}
