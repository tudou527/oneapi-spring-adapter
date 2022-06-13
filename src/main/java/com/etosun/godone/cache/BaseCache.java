package com.etosun.godone.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.ArrayList;

public class BaseCache<T> {
    Cache cache;
    CacheManager cacheManager = CacheManager.create("./src/main/resources/ehcache.xml");

    public BaseCache() {
        cache = cacheManager.getCache("ResourceCache");
    }

    // 为了 mock 这里强行设置返回值
    public String setCache(String classPath, String filePath) {
        if (getCache(classPath) == null) {
            cache.put(new Element(classPath, filePath));
        }
        return classPath;
    }

    public ArrayList<String> getCache() {
        try {
            return (ArrayList<String>) cache.getKeys();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public T getCache(String key) {
        try {
            Element value = cache.get(key);
    
            if (value != null) {
                return (T) value.getObjectValue();
            }
            
            return null;
        } catch (IllegalStateException ignore) {
            return null;
        }
    }

    public void removeCache(String cacheKey) {
        cache.remove(cacheKey);
    }

    public void clearCache(Boolean deleteAnalysisResult) {
        String[] cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            if (!cacheName.equals("JavaModelCache") || deleteAnalysisResult) {
                cacheManager.removeCache(cacheName);
            }
        }
    }
}
