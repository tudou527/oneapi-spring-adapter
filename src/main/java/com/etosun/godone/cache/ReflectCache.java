package com.etosun.godone.cache;

import com.google.inject.Singleton;

@Singleton
public class ReflectCache extends BaseCache<String> {

    public ReflectCache() {
        cache = cacheManager.getCache("ReflectClassCache");
    }
}
