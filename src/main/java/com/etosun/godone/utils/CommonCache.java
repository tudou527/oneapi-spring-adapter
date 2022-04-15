package com.etosun.godone.utils;

import com.etosun.godone.models.JavaFileModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// 内存缓存
@Singleton
public class CommonCache {
    private String projectPath;
    private String localRepository;
    
    // 入口文件 (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> entry = new ConcurrentHashMap<>();
    // 资源文件 (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> resource = new ConcurrentHashMap<>();
    // 本地 mvn 仓库下通过反射获取的 classPath (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> reflectClassPath = new ConcurrentHashMap<>();
    // 已解析的结果 classPath (key 为 classPath value 为 解析结果)
    private final ConcurrentHashMap<String, JavaFileModel> fileModel = new ConcurrentHashMap<>();
    // 待处理的队列
    private final ConcurrentHashMap<String, String> paddingClassPath = new ConcurrentHashMap<>();
    
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
    public void setLocalRepository(String localRepository) {
        this.localRepository = localRepository;
    }
    
    public Collection<String> getEntry() {
        return entry.values();
    }
    public void saveEntry(String classPath, String filePath) {
        if (classPath == null || filePath == null) {
            return;
        }
        entry.put(classPath, filePath);
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
    public void saveReflectClass(String classPath, String filePath) {
        if (classPath == null || filePath == null) {
            return;
        }
        reflectClassPath.putIfAbsent(classPath, filePath);
    }

    public List<String> getPaddingClassPath() {
        return new ArrayList<>(paddingClassPath.keySet());
    }
    public void removePaddingClassPath(String classPath) {
        paddingClassPath.remove(classPath);
    }
    public void savePaddingClassPath(String classPath) {
        if (fileModel.get(classPath) == null) {
            paddingClassPath.put(classPath, classPath);
        }
    }
    
}
