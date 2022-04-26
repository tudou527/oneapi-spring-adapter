package com.etosun.godone.cache;

import com.etosun.godone.analysis.TypeAnalysis;
import com.google.inject.Singleton;
import net.sf.ehcache.Element;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class PendingCache extends BaseCache<String> {
    public List<String> blackListClassPrefix = new ArrayList<String>() {{
        add("java.");
        add("String");
        add("boolean");
        add("javax.");
        add("void");
        add("org.springframework.");
        add("org.slf4j.");
    }};
    
    public PendingCache() {
        cache = cacheManager.getCache("PendingClassCache");
    }
    
    public void setCache(String classPath) {
        if (blackListClassPrefix.stream().noneMatch(classPath::startsWith) && classPath.length() != 1) {
            cache.put(new Element(classPath, classPath));
        }
    }
    
}
