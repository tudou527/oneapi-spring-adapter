package com.oneapi.spring.test.util.mavenUtil;

import com.oneapi.spring.cache.ReflectCache;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.utils.MavenUtil;
import com.oneapi.spring.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@DisplayName("mavenUtil.saveReflectClassCache")
public class ReflectClassTest {
    @Mock
    Logger log;
    @Mock
    FileUtil fileUtil;
    @Mock
    ReflectCache reflectCache;
    @InjectMocks
    MavenUtil mvnUtil;
    
    List<String> fileList;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        FileUtil fUtil = new FileUtil();
        fileList = fUtil.findFileList("glob:**/*.java", TestUtil.getBaseDir());
    
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }
    
    @Test
    @DisplayName("缓存 jar 包中的 class")
    public void saveReflectClass() {
        List<String> jarList = new ArrayList<String>() {{
            add(TestUtil.currentDir + "/lib/chardet-1.0.jar");
        }};
        Mockito.when(fileUtil.findFileList(Mockito.anyString(), Mockito.anyString())).thenReturn(jarList);

        // 保存 resource 缓存调用参数
        HashMap<String, String> reflectCacheData = new HashMap<>();
        Mockito.when(reflectCache.setCache(Mockito.anyString(), Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            reflectCacheData.put((String) args[0], (String) args[1]);
            return null;
        });
    
        mvnUtil.saveReflectClassCache("");
    
        Assertions.assertEquals(reflectCacheData.size(), 29);
    }
    
    @Test
    @DisplayName("异常处理")
    public void throwError() {
        // 先注释掉，这里 mock JarFile 的异常会导致其他用例执行报错
//        List<String> jarList = new ArrayList<String>() {{
//            add(TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/guice-4.2.3.jar");
//        }};
//        Mockito.when(fileUtil.findFileList(Mockito.anyString(), Mockito.anyString())).thenReturn(jarList);
//
//        // 保存 resource 缓存调用参数
//        HashMap<String, String> reflectCacheData = new HashMap<>();
//        Mockito.when(reflectCache.setCache(Mockito.anyString(), Mockito.anyString())).then((Answer<String>) invocation -> {
//            Object[] args = invocation.getArguments();
//            reflectCacheData.put((String) args[0], (String) args[1]);
//            return null;
//        });
//        // mock JarFile
//        Mockito.mockConstruction(JarFile.class, (mock, context) -> {
//            Mockito.when(mock.entries()).thenThrow(new Exception("error."));
//        });
//
//        mvnUtil.saveReflectClassCache("");
//
//        Assertions.assertEquals(reflectCacheData.size(), 0);
//
//        Mockito.reset(new ArrayList<Class<?>>(){{
//            add(JarFile.class);
//        }});
    }
}
