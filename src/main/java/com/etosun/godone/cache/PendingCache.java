package com.etosun.godone.cache;

import com.google.inject.Singleton;
import net.sf.ehcache.Element;

@Singleton
public class PendingCache extends BaseCache<String> {
    
    public PendingCache() {
        cache = cacheManager.getCache("PendingClassCache");
    }
    
    public void setCache(String classPath) {
        cache.put(new Element(classPath, classPath));
    }
    
}
