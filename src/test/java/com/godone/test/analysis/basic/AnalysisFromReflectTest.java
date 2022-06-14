package com.godone.test.analysis.basic;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.cache.ResourceCache;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.godone.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@DisplayName("basic.analysis.analysisFromReflect")
public class AnalysisFromReflectTest {
    @Mock
    FileUtil fileUtil;
    @Mock
    MavenUtil mvnUtil;
    @Mock
    ResourceCache resourceCache;
    @Mock
    ReflectCache reflectCache;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("未能从缓存的 .jar 包中匹配到 class")
    public void notMatchReflectCache() {
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(null);
        
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromReflect", "");

        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("通过源码 .jar 匹配 class")
    public void sourceJARExist() throws IOException {
        // mock 解压 zip，断言参数
        HashMap<String, String> unZipArgs = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            unZipArgs.put(((File) args[0]).getAbsolutePath(), (String) args[1]);
            return null;
        }).when(fileUtil).unzipJar(Mockito.any(), Mockito.any());
        
        // mock saveResource 方法，断言参数
        HashMap<String, Boolean> resourceArgs = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            resourceArgs.put((String) args[0], (Boolean) args[1]);
            return null;
        }).when(mvnUtil).saveResource(Mockito.any(), Mockito.anyBoolean());
    
        // 返回 null 避免重新执行 analysisFromResource 方法
        Mockito.when(resourceCache.getCache(Mockito.any())).thenReturn(null);
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
        
        // 复制并重命名 guice-4.2.3.jar 为 guice-4.2.3-sources.jar
        File jarFile = new File(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
        File sourceJarFile = new File(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3-sources.jar");
        FileCopyUtils.copy(jarFile, sourceJarFile);
    
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromReflect", "com.google.inject.spi.ElementVisitor");
        
        Assertions.assertNull(classModel);
        // 断言 unzip 参数
        String unzipKey = TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3-sources.jar";
        Assertions.assertEquals(unZipArgs.get(unzipKey), TestUtil.getBaseDir() + "com/godone/testSuite/source");
        
        // 断言 saveResource 参数
        Assertions.assertFalse(resourceArgs.get(TestUtil.getBaseDir() + "com/godone/testSuite/source"));
        
        // 删除复制的文件
        if (sourceJarFile.exists()) {
            sourceJarFile.delete();
        }
    }
    
    @Test
    @DisplayName("通过反编译匹配 .jar 包中的 class")
    public void notInSourceJAR() {
        HashMap<String, Boolean> resourceMap = new HashMap<>();
        // mock 保存资源的接口
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            resourceMap.put((String) args[0], (Boolean) args[1]);
            return null;
        }).when(mvnUtil).saveResource(Mockito.anyString(), Mockito.anyBoolean());
        
        // mock 解压文件
        HashMap<String, String[]> unzipArgs = new HashMap<>();
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            unzipArgs.put((String) args[1], (String[]) args[0]);
            return null;
        }).when(fileUtil).exec(Mockito.any(), Mockito.any());

        // 返回 null 避免重新执行 analysisFromResource 方法
        Mockito.when(resourceCache.getCache(Mockito.any())).thenReturn(null);
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
        
        // 创建 decompile 目录，用于断言是否会删除
        File deCompileDir = new File(TestUtil.getBaseDir() + "com/godone/testSuite/deCompile");
        if (!deCompileDir.exists()) {
            deCompileDir.mkdirs();
        }

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromReflect", "com.google.inject.spi.ElementVisitor");
        
        // deCompileDir 应该被删除
        Assertions.assertFalse(deCompileDir.exists());
    
        // 因为 resourceCache.getCache mock 返回了 null 所以这里肯定是 null
        Assertions.assertNull(classModel);
        
        // 断言 保存资源的接口 的参数
        Assertions.assertFalse(resourceMap.get(deCompileDir.getAbsolutePath()));
        // 断言解压参数
        String unzipKey = TestUtil.getBaseDir() + "com/godone/testSuite";
        Assertions.assertEquals(String.join(" ", unzipArgs.get(unzipKey)), String.join("", "java -jar /Users/xiaoyun/github/godone/src/main/resources/lib/procyon-decompiler.jar -jar " + TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar -o "+ TestUtil.getBaseDir() + "com/godone/testSuite/deCompile"));
    }
}
