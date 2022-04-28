package com.etosun.godone.cache;

import com.google.inject.Singleton;

@Singleton
public class TestCaseCache extends BaseCache<String> {
    
    public TestCaseCache() {
        cache = cacheManager.getCache("TestCaseCache");
    }
}
