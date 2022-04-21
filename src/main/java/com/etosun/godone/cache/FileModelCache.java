package com.etosun.godone.cache;

import com.etosun.godone.models.JavaFileModel;
import net.sf.ehcache.Element;

public class FileModelCache extends BaseCache<JavaFileModel> {
    
    public FileModelCache() {
        cache = cacheManager.getCache("JavaModelCache");
    }
    
    public void setCache(JavaFileModel fileModel) {
        String key = fileModel.getClassModel().getClassPath();
        cache.put(new Element(key, fileModel));
    }
}
