package com.godone.test.cache;

import com.etosun.godone.cache.*;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import net.sf.ehcache.CacheManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@DisplayName("clearCache")
public class ClearCacheTest {
    @Mock
    CacheManager cacheManager;
    @InjectMocks
    PendingCache pendingCache;
    @InjectMocks
    EntryCache entryCache;
    @InjectMocks
    ResourceCache resourceCache;
    @InjectMocks
    ReflectCache reflectCache;
    @InjectMocks
    FileModelCache fileModeCache;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        Mockito.when(cacheManager.getCache(Mockito.anyString())).thenCallRealMethod();
    
        JavaClassModel classModel = new JavaClassModel();
        classModel.setClassPath("com.godone.test.a");
        classModel.setName("TestFileModel");
        classModel.setDescription(null);
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setClassModel(classModel);
    
        pendingCache.setCache("a.b.c.d");
        fileModeCache.setCache(fileModel);
        entryCache.setCache("com.godone.test.a", "file://a/b");
        resourceCache.setCache("com.godone.test.a", "file://filepath");
        reflectCache.setCache("com.godone.test.a", "file://filepath");
    }

    @Test
    @DisplayName("清空缓存")
    public void clear() {
        pendingCache.clear();
        entryCache.clear();
        resourceCache.clear();
        reflectCache.clear();

        Assertions.assertNull(pendingCache.getCache("a.b.c.d"));
        Assertions.assertNull(entryCache.getCache("com.godone.test.a"));
        Assertions.assertNull(resourceCache.getCache("com.godone.test.a"));
        Assertions.assertNull(reflectCache.getCache("com.godone.test.a"));
    
        Assertions.assertNotNull(fileModeCache.getCache("com.godone.test.a"));
    
        fileModeCache.clear();
        Assertions.assertNull(fileModeCache.getCache("com.godone.test.a"));
    }
}
