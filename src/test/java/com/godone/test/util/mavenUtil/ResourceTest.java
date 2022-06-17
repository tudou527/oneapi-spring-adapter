package com.godone.test.util.mavenUtil;

import com.godone.meta.cache.EntryCache;
import com.godone.meta.cache.ResourceCache;
import com.godone.meta.utils.FileUtil;
import com.godone.meta.utils.MavenUtil;
import com.godone.test.TestUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@DisplayName("mavenUtil.saveResource")
public class ResourceTest {
    @Mock
    FileUtil fileUtil;
    @Mock
    EntryCache entryCache;
    @Mock
    ResourceCache resourceCache;
    @InjectMocks
    MavenUtil mvnUtil;
    
    List<String> fileList;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        FileUtil fUtil = new FileUtil();
        fileList = fUtil.findFileList("glob:**/*.java", TestUtil.getBaseDir());
    }
    
    @Test
    @DisplayName("缓存为资源")
    public void saveResource() {
        Mockito.when(fileUtil.findFileList(Mockito.anyString(), Mockito.anyString())).thenReturn(fileList);
        Mockito.when(fileUtil.getBuilder(Mockito.anyString())).thenAnswer((Answer<JavaProjectBuilder>) invocation -> {
            String filePath = invocation.getArgument(0);
            JavaProjectBuilder builder = new JavaProjectBuilder();
            builder.addSource(new File(filePath));
            return builder;
        });
        
        // 保存 resource 缓存调用参数
        HashMap<String, String> resourceCacheData = new HashMap<>();
        Mockito.when(resourceCache.setCache(Mockito.anyString(), Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            resourceCacheData.put((String) args[0], (String) args[1]);
            return null;
        });
        // 保存 entry 缓存调用参数
        HashMap<String, String> entryCacheData = new HashMap<>();
        Mockito.when(entryCache.setCache(Mockito.anyString(), Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            entryCacheData.put((String) args[0], (String) args[1]);
            return null;
        });

        mvnUtil.saveResource(TestUtil.getBaseDir(), false);
        
        // 资源缓存数量 > 0
        Assertions.assertTrue(resourceCacheData.size() > 1);
        // 缓存 key 为 classPath
        resourceCacheData.keySet().forEach(key -> {
            Assertions.assertTrue(key.startsWith("com."));
            Assertions.assertFalse(key.contains("/"));
        });
        // 缓存 value 为 filePath
        resourceCacheData.values().forEach(key -> {
            Assertions.assertTrue(key.startsWith("/"));
            Assertions.assertFalse(key.contains("com."));
        });
        // 入口缓存不存在
        Assertions.assertEquals(entryCacheData.size(), 0);
    }
    
    @Test
    @DisplayName("缓存为入口")
    public void saveEntry() {
        Mockito.when(fileUtil.findFileList(Mockito.anyString(), Mockito.anyString())).thenReturn(fileList);
        Mockito.when(fileUtil.getBuilder(Mockito.anyString())).thenAnswer((Answer<JavaProjectBuilder>) invocation -> {
            String filePath = invocation.getArgument(0);
            JavaProjectBuilder builder = new JavaProjectBuilder();
            builder.addSource(new File(filePath));
            return builder;
        });
        
        // 保存 resource 缓存调用参数
        HashMap<String, String> resourceCacheData = new HashMap<>();
        Mockito.when(resourceCache.setCache(Mockito.anyString(), Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            resourceCacheData.put((String) args[0], (String) args[1]);
            return null;
        });
        // 保存 entry 缓存调用参数
        HashMap<String, String> entryCacheData = new HashMap<>();
        Mockito.when(entryCache.setCache(Mockito.anyString(), Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            entryCacheData.put((String) args[0], (String) args[1]);
            return null;
        });
        
        mvnUtil.saveResource(TestUtil.getBaseDir(), true);
        
        // 入口缓存数量 > 0
        Assertions.assertTrue(entryCacheData.size() > 0);
        // 缓存 key 为 classPath
        entryCacheData.keySet().forEach(key -> {
            Assertions.assertTrue(key.startsWith("com."));
            Assertions.assertFalse(key.contains("/"));
        });
        // 缓存 value 为 filePath
        entryCacheData.values().forEach(key -> {
            Assertions.assertTrue(key.startsWith("/"));
            Assertions.assertFalse(key.contains("com."));
        });
        // 资源缓存不存在
        Assertions.assertTrue(resourceCacheData.size() > 1);
    }
}
