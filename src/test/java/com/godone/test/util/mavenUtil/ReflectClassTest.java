package com.godone.test.util.mavenUtil;

import com.etosun.godone.cache.ReflectCache;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;

@DisplayName("mavenUtil.saveReflectClassCache")
public class ReflectClassTest {
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
    }
    
    @Test
    @DisplayName("缓存 jar 包中的 class")
    public void saveReflectClass() {
        List<String> jarList = new ArrayList<String>() {{
            add(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
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
    
        Assertions.assertEquals(reflectCacheData.size(), 585);
    }
    
    @Test
    @DisplayName("异常处理")
    public void throwError() {
        // 先注释掉，这里 mock JarFile 的异常会导致其他用例执行报错
//        List<String> jarList = new ArrayList<String>() {{
//            add(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
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
