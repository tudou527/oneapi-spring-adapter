package com.oneapi.spring.test.cache;

import com.oneapi.spring.cache.*;
import com.oneapi.spring.models.JavaClassModel;
import com.oneapi.spring.models.JavaFileModel;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@DisplayName("cache.set")
public class NormalTest {
    @InjectMocks
    BaseCache baseCache;
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
    @DisplayName("缓存待解析的 class")
    public void pendingCache() {
        pendingCache.setCache("java.util.String");
        pendingCache.setCache("String");
        pendingCache.setCache("boolean");
        pendingCache.setCache("javax.a");
        pendingCache.setCache("void");
        pendingCache.setCache("org.springframework.a");
        pendingCache.setCache("org.slf4j.b");
        pendingCache.setCache("com.oneapi.spring.testSuite.demo");
    
        Assertions.assertNull(pendingCache.getCache("java.util.String"));
        Assertions.assertNull(pendingCache.getCache("org.springframework.a"));
        // 默认是待解析状态
        Assertions.assertEquals(pendingCache.getCache("com.oneapi.spring.testSuite.demo"), "wait");
    }
    
    @Test
    @DisplayName("缓存解析结果")
    public void fileModeCache() {
        JavaClassModel classModel = new JavaClassModel();
        classModel.setClassPath("com.oneapi.spring.test.a");
        classModel.setName("TestFileModel");
        classModel.setDescription(null);

        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setClassModel(classModel);
        fileModeCache.setCache(fileModel);
    
        JavaFileModel model = fileModeCache.getCache("com.oneapi.spring.test.a");
        
        // 不缓存 javaSource
        Assertions.assertNull(model.getJavaSource());

        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getClassModel());
        Assertions.assertEquals(model.getClassModel().getName(), "TestFileModel");
    }
    
    @Test
    @DisplayName("缓存入口 class")
    public void entryCache() {
        entryCache.setCache("com.oneapi.spring.test.a", "file://a/b");
        entryCache.setCache("com.oneapi.spring.test.b.c", "file://b/c");
    
        ArrayList<String> filePath = entryCache.getCache();
        Assertions.assertEquals(filePath, new ArrayList<String>(){{
            add("com.oneapi.spring.test.b.c");
            add("com.oneapi.spring.test.a");
        }});
    }

    @Test
    @DisplayName("缓存资源 class")
    public void resourceCache() {
        resourceCache.setCache("com.oneapi.spring.test.a", "file://filepath");
        
        String filePath = resourceCache.getCache("com.oneapi.spring.test.a");
        Assertions.assertNotNull(filePath, "file://filepath");
    }
    
    @Test
    @DisplayName("缓存通过反射获取到的 class")
    public void reflectCache() {
        reflectCache.setCache("com.oneapi.spring.test.a", "file://filepath");
        
        String filePath = reflectCache.getCache("com.oneapi.spring.test.a");
        Assertions.assertNotNull(filePath, "file://filepath");
    }
}
