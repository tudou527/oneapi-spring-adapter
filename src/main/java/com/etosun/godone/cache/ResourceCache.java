package com.etosun.godone.cache;

import com.google.inject.Singleton;

@Singleton
public class ResourceCache extends BaseCache<String> {

    public ResourceCache() {
        cache = cacheManager.getCache("ResourceCache");
    }
}
