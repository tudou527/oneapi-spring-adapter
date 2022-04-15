package com.etosun.godone.analysis;

import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.CommonCache;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaType;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JAR 包资源解析
 */
public class ReflectAnalysis {
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
    @Inject
    private CommonCache commonCache;
    @Inject
    Provider<BasicAnalysis> basicAnalysis;

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
        
        String packageName = classPath.substring(classPath.lastIndexOf(".") + 1);
        fileModel.setPackageName(packageName);

        Class<?> targetClass = mvnUtil.getMatchReflectClass(classPath, jarFilePath);
        if (targetClass != null) {
            JavaClassModel classModel = new JavaClassModel();
            classModel.setName(targetClass.getName());
            classModel.setClassPath(classPath);
        }
        
        return fileModel;
    }

    
}
