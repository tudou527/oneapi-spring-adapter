package com.oneapi.spring.cache;

import com.oneapi.spring.models.JavaFileModel;
import net.sf.ehcache.Element;

public class FileModelCache extends BaseCache<JavaFileModel> {
    
    public FileModelCache() {
        cache = cacheManager.getCache("JavaModelCache");
    }
    
    public void setCache(JavaFileModel fileModel) {
        String key = fileModel.getClassModel().getClassPath();
        
        // 删除 javaSource（这个字段只在解析过程中会被用到）
        fileModel.setJavaSource(null);
        
        cache.put(new Element(key, fileModel));
    }
}
