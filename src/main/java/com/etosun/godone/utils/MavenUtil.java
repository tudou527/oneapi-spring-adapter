package com.etosun.godone.utils;

import com.google.inject.Inject;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Singleton
public class MavenUtil {
    @Inject
    private FileUtil fileUtil;
    @Inject
    private Logger logger;
    @Inject
    private CommonCache commonCache;

    // 缓存入口文件及其他资源文件
    public void saveResource(String entryDir, boolean saveAsEntry) {
        fileUtil.findFileList("glob:**/*.java", entryDir).forEach(filePath -> {
            JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
            if (builder != null) {
                Optional<JavaClass> optionalJavaClass = builder.getClasses().stream().filter(JavaClass::isPublic).findFirst();
    
                if (optionalJavaClass.isPresent()) {
                    JavaClass javaClass = optionalJavaClass.get();
                    String className = String.format("%s.%s", javaClass.getPackageName(), javaClass.getName());
        
                    // 缓存为资源文件
                    commonCache.saveResource(className, filePath);
                    boolean hasEntryAnnotation = javaClass.getAnnotations().stream().anyMatch(an -> an.getType().getName().endsWith("Controller"));
                    // 缓存为入口
                    if (saveAsEntry && hasEntryAnnotation) {
                        logger.message("cache entry: %s", className);
                        commonCache.saveEntry(className, filePath);
                    }
                }
            }
        });
    }

    // 缓存所有 jar 包中的类
    public void saveReflectClassCache(String localRepository) {
        fileUtil.findFileList("glob:**/*.jar", localRepository).forEach(jarFilePath -> {
            // 跳过源码JAR
            if (!jarFilePath.contains("-sources.jar")) {
                // 通过反射获取 jar 包中所有 classPath
                try {
                    JarFile jarFile = new JarFile(jarFilePath);
                    Enumeration<JarEntry> entries = jarFile.entries();
                    JarEntry entry;

                    while (entries.hasMoreElements()) {
                        entry = entries.nextElement();

                        if (!entry.getName().contains("META-INF") && entry.getName().contains(".class")) {
                            String classPath = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                            commonCache.saveReflectClass(classPath, jarFilePath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 返回 class 中所有 field （包括 parent class）
    public List<Field> getFieldList(Class<?> cls) {
        List<Field> fieldList = new ArrayList<>();

        while (cls.getSuperclass() != null) {
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        return fieldList;
    }

    // 为 .* 的模糊 import 补全路径
    public List<String> getFuzzyImportPackage(String fuzzyImport) {
        String importPrefix = fuzzyImport.substring(0, fuzzyImport.length() - 1);
        List<String> reflectClassList = new ArrayList<>();

        // 尝试匹配 JAR 包中的 classPath
        commonCache.getReflectClass().keySet().forEach(classPath -> {
            if (classPath.startsWith(importPrefix)) {
                reflectClassList.add(classPath);
            }
        });

        // 尝试从当前项目资源文件匹配
        commonCache.getResource().keySet().forEach(classPath -> {
            if (classPath.startsWith(importPrefix)) {
                reflectClassList.add(classPath);
            }
        });

        return reflectClassList;
    }
    
    // 通过反射获取 jar 中与 classPath 的所有 class
    public Class<?> getMatchReflectClass(String classPath, String jarFilePath) {
        Class<?> matchClass = null;
        
        try {
            JarEntry entry;
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            
            URL[] urls = new URL[]{ new URL("file:"+ jarFilePath) };
            URLClassLoader childClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (!entry.getName().contains("META-INF") && entry.getName().contains(".class")) {
                    String className = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                    
                    if (className.equals(classPath)) {
                        try {
                            matchClass = Class.forName(className, true, childClassLoader);
                        } catch (NoClassDefFoundError ignore) {
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        
        return matchClass;
    }
}
