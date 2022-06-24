package com.godone.meta.cache;

import com.google.inject.Singleton;
import net.sf.ehcache.Element;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class PendingCache extends BaseCache<String> {
    public static List<String> blackListClassPrefix = new ArrayList<String>() {{
        add("java.");
        add("javax.");
        add("void");
        add("org.springframework.");
        add("org.slf4j.");
        
        // 内置类型
        add("byte");
        add("short");
        add("int");
        add("long");
        add("float");
        add("double");
        add("boolean");
        add("char");
    }};
    
    public PendingCache() {
        cache = cacheManager.getCache("PendingClassCache");
    }
    
    public void setCache(String classPath) {
        if (
            blackListClassPrefix.stream().noneMatch(classPath::startsWith) &&
            classPath.length() != 1 &&
            !getCache().contains(classPath)
        ) {
            // 设置为待解析状态
            cache.put(new Element(classPath, "wait"));
        }
    }
    
    public void updateCache(String classPath) {
        String cacheValue = getCache(classPath);
        if ("wait".equals(cacheValue)) {
            cache.put(new Element(classPath, "done"));
        }
    }
}
