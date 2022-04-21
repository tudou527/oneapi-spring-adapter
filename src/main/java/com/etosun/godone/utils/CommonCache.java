package com.etosun.godone.utils;

import com.google.inject.Singleton;

import java.util.concurrent.ConcurrentHashMap;

// 内存缓存
@Singleton
public class CommonCache {
    // 本地 mvn 仓库下通过反射获取的 classPath (key 为 classPath value 为 filePath)
    private final ConcurrentHashMap<String, String> reflectClassPath = new ConcurrentHashMap<>();

}
