package com.godone.test.cache;

import com.etosun.godone.cache.*;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@DisplayName("clearCache")
public class ClearCacheTest {
    @InjectMocks
    BaseCache baseCache;
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
    
        JavaClassModel classModel = new JavaClassModel();
        classModel.setClassPath("com.godone.test.a");
        classModel.setName("TestFileModel");
        classModel.setDescription(null);
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setClassModel(classModel);
    
        fileModeCache.setCache(fileModel);
        pendingCache.setCache("a.b.c.d");
        entryCache.setCache("com.godone.test.a", "file://a/b");
        resourceCache.setCache("com.godone.test.a", "file://filepath");
        reflectCache.setCache("com.godone.test.a", "file://filepath");
    }

    @Test
    @DisplayName("清空缓存")
    public void clear() {
        pendingCache.clearCache(false);

        Assertions.assertNull(pendingCache.getCache("a.b.c.d"));
        Assertions.assertNull(entryCache.getCache("com.godone.test.a"));
        Assertions.assertNull(resourceCache.getCache("com.godone.test.a"));
        Assertions.assertNull(reflectCache.getCache("com.godone.test.a"));
    
        Assertions.assertNotNull(fileModeCache.getCache("com.godone.test.a"));
    
        baseCache.clearCache(true);
        
        Assertions.assertNull(fileModeCache.getCache("com.godone.test.a"));
    }
}
