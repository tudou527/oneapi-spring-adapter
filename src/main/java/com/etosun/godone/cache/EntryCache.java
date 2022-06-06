package com.etosun.godone.cache;

import com.google.inject.Singleton;

@Singleton
public class EntryCache extends BaseCache<String> {
    
    public EntryCache() {
        cache = cacheManager.getCache("EntryCache");
    }
}