package com.etosun.godone.utils;

import com.etosun.godone.models.JavaFileModel;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// 内存缓存
@Singleton
public class CommonCache {
    // 入口文件 (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> entry = new ConcurrentHashMap<>();
    // 资源文件 (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> resource = new ConcurrentHashMap<>();
    // 本地 mvn 仓库下通过反射获取的 classPath (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> reflectClassPath = new ConcurrentHashMap<>();
    // 已解析的结果 classPath (key 为 classPath value 为 解析结果)
    private final ConcurrentHashMap<String, JavaFileModel> fileModel = new ConcurrentHashMap<>();
    // 待处理的队列 (1: 待解析 2：完成）
    private final ConcurrentHashMap<String, Integer> paddingClassPath = new ConcurrentHashMap<>();

    public Collection<String> getEntry() {
        return entry.values();
    }
    public void saveEntry(String classPath, String filePath) {
        if (classPath == null || filePath == null) {
            return;
        }
        entry.put(filePath, filePath);
    }

    public ConcurrentHashMap<String, String> getResource() {
        return resource;
    }
    public String getResource(String classPath) {
        return resource.get(classPath);
    }
    public void saveResource(String classPath, String filePath) {
        if (classPath == null || filePath == null) {
            return;
        }
        resource.put(classPath, filePath);
    }

    //
    public JavaFileModel getModel(String classPath) {
        if (classPath == null) {
            return null;
        }
        return fileModel.get(classPath);
    }
    public ConcurrentHashMap<String, JavaFileModel> getModel() {
        return fileModel;
    }
    public void saveModel(String classPath, JavaFileModel model) {
        if (classPath == null || model == null) {
            return;
        }
        fileModel.putIfAbsent(classPath, model);
    }

    public ConcurrentHashMap<String, String> getReflectClass() {
        return reflectClassPath;
    }
    public String getReflectClass(String classPath) {
        return reflectClassPath.get(classPath);
    }
    public void saveReflectClass(String classPath, String filePath) {
        if (classPath == null || filePath == null) {
            return;
        }
        reflectClassPath.putIfAbsent(classPath, filePath);
    }

    public List<String> getPaddingClassPath() {
        return paddingClassPath.keySet().stream().filter(k -> paddingClassPath.get(k) == 1).collect(Collectors.toList());
    }
    public Integer getPaddingClassPath(String classPath) {
        return paddingClassPath.get(classPath);
    }
    public void savePaddingClassPath(String classPath, Integer statusVal) {
        if (classPath == null) {
            return;
        }
        paddingClassPath.put(classPath, statusVal);
    }
    
}
