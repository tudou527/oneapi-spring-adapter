package com.oneapi.spring.test.cache;

import com.oneapi.spring.cache.ReflectCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@DisplayName("cache.removeCache")
public class RemoveCacheTest {
    @InjectMocks
    ReflectCache reflectCache;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("按 key 删除缓存")
    public void removeCacheByKey() {
        reflectCache.setCache("com.oneapi.spring.test.a", "file://filepath");
        String filePath = reflectCache.getCache("com.oneapi.spring.test.a");
        Assertions.assertNotNull(filePath, "file://filepath");
    
        reflectCache.removeCache("com.oneapi.spring.test.a");
        Assertions.assertNull(reflectCache.getCache("com.oneapi.spring.test.a"));
    }
}
