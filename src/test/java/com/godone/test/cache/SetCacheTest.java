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

import java.util.ArrayList;

@DisplayName("cache.set")
public class SetCacheTest {
    @InjectMocks
    PendingCache pendingCache;
    @InjectMocks
    FileModelCache fileModeCache;
    @InjectMocks
    EntryCache entryCache;
    @InjectMocks
    ResourceCache resourceCache;
    @InjectMocks
    ReflectCache reflectCache;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("pendingCache")
    public void pendingCache() {
        pendingCache.setCache("java.util.String");
        pendingCache.setCache("String");
        pendingCache.setCache("boolean");
        pendingCache.setCache("javax.a");
        pendingCache.setCache("void");
        pendingCache.setCache("org.springframework.a");
        pendingCache.setCache("org.slf4j.b");
        pendingCache.setCache("com.godone.testSuite.demo");
    
        Assertions.assertNull(pendingCache.getCache("java.util.String"));
        Assertions.assertNull(pendingCache.getCache("org.springframework.a"));
        Assertions.assertEquals(pendingCache.getCache("com.godone.testSuite.demo"), "com.godone.testSuite.demo");
    }
    
    @Test
    @DisplayName("fileModeCache")
    public void fileModeCache() {
        JavaClassModel classModel = new JavaClassModel();
        classModel.setClassPath("com.godone.test.a");
        classModel.setName("TestFileModel");
        classModel.setDescription(null);

        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setClassModel(classModel);
        fileModeCache.setCache(fileModel);
    
        JavaFileModel model = fileModeCache.getCache("com.godone.test.a");
        
        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getClassModel());
        Assertions.assertEquals(model.getClassModel().getName(), "TestFileModel");
    }
    
    @Test
    @DisplayName("entryCache")
    public void entryCache() {
        entryCache.setCache("com.godone.test.a", "file://a/b");
        entryCache.setCache("com.godone.test.b.c", "file://b/c");
    
        ArrayList<String> filePath = entryCache.getCache();
        Assertions.assertEquals(filePath, new ArrayList<String>(){{
            add("com.godone.test.b.c");
            add("com.godone.test.a");
        }});
    }
    
    @Test
    @DisplayName("resourceCache")
    public void resourceCache() {
        resourceCache.setCache("com.godone.test.a", "file://filepath");
        
        String filePath = resourceCache.getCache("com.godone.test.a");
        Assertions.assertNotNull(filePath, "file://filepath");
    }
    
    @Test
    @DisplayName("reflectCache")
    public void reflectCache() {
        reflectCache.setCache("com.godone.test.a", "file://filepath");
        
        String filePath = reflectCache.getCache("com.godone.test.a");
        Assertions.assertNotNull(filePath, "file://filepath");
    }
}
