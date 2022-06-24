package com.godone.meta.utils;

import com.godone.meta.cache.EntryCache;
import com.godone.meta.cache.ReflectCache;
import com.godone.meta.cache.ResourceCache;
import com.google.inject.Inject;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Singleton
public class MavenUtil {
    @Inject
    private Logger log;
    @Inject
    private FileUtil fileUtil;
    @Inject
    private EntryCache entryCache;
    @Inject
    private ReflectCache reflectCache;
    @Inject
    private ResourceCache resourceCache;

    // 缓存入口文件及其他资源文件
    public void saveResource(String entryDir, boolean saveAsEntry) {
        AtomicInteger entryCount = new AtomicInteger();
        List<String> javaFiles = fileUtil.findFileList("glob:**/*.java", entryDir);
    
        log.info("found %s class from %s", javaFiles.size(), entryDir);
    
        javaFiles.forEach(filePath -> {
            JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
            if (builder != null) {
                Optional<JavaClass> optionalJavaClass = builder.getClasses().stream().filter(JavaClass::isPublic).findFirst();
    
                if (optionalJavaClass.isPresent()) {
                    JavaClass javaClass = optionalJavaClass.get();
                    String className = String.format("%s.%s", javaClass.getPackageName(), javaClass.getName());
    
                    // 缓存为资源文件
                    resourceCache.setCache(className, filePath);
                    
                    // 缓存为入口
                    if (saveAsEntry) {
                        boolean hasEntryAnnotation = javaClass.getAnnotations().stream().anyMatch(an -> an.getType().getName().endsWith("Controller"));
                        if (hasEntryAnnotation) {
                            entryCount.set(entryCount.get() + 1);
                            entryCache.setCache(className, filePath);
                        }
                    }
                }
            }
        });
    
        log.info("found %s entry class", entryCount.get());
    }

    // 缓存所有 jar 包中的类
    public void saveReflectClassCache(String localRepository) {
        AtomicInteger cacheCount = new AtomicInteger();
        List<String> jarList = fileUtil.findFileList("glob:**/*.jar", localRepository)
                // 忽略源码 jar
                .stream().filter(p -> !p.contains("-source.jar")).collect(Collectors.toList());
        
        log.info("found %s jar file from %s", jarList.size(), localRepository);

        jarList.forEach(jarFilePath -> {
            // 通过反射获取 jar 包中所有 classPath
            try {
                JarFile jarFile = new JarFile(jarFilePath);
                Enumeration<JarEntry> entries = jarFile.entries();
                JarEntry entry;

                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();

                    if (!entry.getName().contains("META-INF") && entry.getName().contains(".class")) {
                        String classPath = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                        cacheCount.set(cacheCount.get() + 1);
                        reflectCache.setCache(classPath, jarFilePath);
                    }
                }
            } catch (Exception ignore) {
            }
        });
    
        log.info("cache %s class", cacheCount.get());
    }

    // 返回 class 中所有 field （包括 parent class）
    public List<Field> getFieldList(Class<?> cls) {
        List<Field> fieldList = new ArrayList<>();

        while (cls.getSuperclass() != null) {
            Arrays.stream(cls.getDeclaredFields()).forEach(f -> {
                String fStr = f.toString();
                // 不处理常量与非序列化字段
                if (!fStr.contains(" final ") && !fStr.contains(" transient ")) {
                    fieldList.add(f);
                }
            });
            cls = cls.getSuperclass();
        }

        return fieldList;
    }

    // 为 .* 的模糊 import 补全路径
    public List<String> getFuzzyImportPackage(String fuzzyImport) {
        String importPrefix = fuzzyImport.substring(0, fuzzyImport.length() - 1);
        List<String> reflectClassList = new ArrayList<>();

        // 尝试匹配 JAR 包中的 classPath
        reflectCache.getCache().forEach(classPath -> {
            if (classPath.startsWith(importPrefix)) {
                reflectClassList.add(classPath);
            }
        });

        // 尝试从当前项目资源文件匹配
        resourceCache.getCache().forEach(classPath -> {
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
