package com.etosun.godone.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.ArrayList;

public class BaseCache<T> {
    Cache cache;
    CacheManager cacheManager = CacheManager.create("./src/main/resources/ehcache.xml");

    public BaseCache() {
        cache = cacheManager.getCache("ResourceCache");
    }

    public void setCache(String classPath, String filePath) {
        cache.put(new Element(classPath, filePath));
    }
    
    public ArrayList<String> getCache() {
        try {
            return (ArrayList<String>) cache.getKeys();
        } catch (Exception e) {
            return null;
        }
    }
    
    public T getCache(String key) {
        Element value = cache.get(key);
        
        if (value != null) {
            return (T) value.getObjectValue();
        }
        
        return null;
    }
    
    public void removeCache(String classPath) {
        cache.remove(classPath);
    }
    
    public void clearAll() {
        cacheManager.removeAllCaches();
        System.out.println(":");
    }
}
