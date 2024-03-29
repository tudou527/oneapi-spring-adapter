package com.oneapi.spring.test.application;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.oneapi.spring.Application;
import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.EntryAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.*;
import com.oneapi.spring.models.JavaClassModel;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.test.TestUtil;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.utils.MavenUtil;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@DisplayName("Application")
public class ApplicationTest {
    @Mock
    MavenUtil mvnUtil;
    @Mock
    Logger log;
    @Mock
    FileUtil fileUtil;
    @Mock
    PendingCache pendingCache;
    @Mock
    EntryCache entryCache;
    @Mock
    ResourceCache resourceCache;
    @Mock
    ReflectCache reflectCache;
    @Mock
    FileModelCache fileModelCache;
    @Mock
    BasicAnalysis basicAnalysisReal;
    @Mock
    Provider<BasicAnalysis> basicAnalysis;
    @Mock
    EntryAnalysis entryAnalysis;
    @Mock
    Provider<EntryAnalysis> entryAnalysisProvider;
    @InjectMocks
    Application app;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(entryAnalysisProvider.get()).thenReturn(entryAnalysis);
    }
    
    @Test
    @DisplayName("解析资源")
    public void analysisClassReferenceTest() {
        // mock 待解析的缓存队列
        ArrayList<String> mockPendingCacheList = new ArrayList<String>() {{
            add("com.godone.a.b.c");
            add("com.godone.a.d.f");
        }};
        Mockito.when(pendingCache.getCache()).thenReturn(mockPendingCacheList);
        Mockito.when(pendingCache.getCache(Mockito.anyString())).thenReturn("wait");
        
        // mock 更新缓存
        List<String> updateCacheKey = new ArrayList<>();
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            updateCacheKey.add((String) args[0]);
            return null;
        }).when(pendingCache).updateCache(Mockito.anyString());
        
        // mock 解析结果缓存队列
        List<JavaFileModel> fileModels = new ArrayList<>();
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            fileModels.add((JavaFileModel) args[0]);
            return null;
        }).when(fileModelCache).setCache(Mockito.any());
        
        Mockito.when(basicAnalysisReal.analysis(Mockito.anyString())).thenReturn(new JavaFileModel(){{
            setPackageName("com.godone.a.d");
        }});
        Mockito.when(basicAnalysis.get()).thenReturn(basicAnalysisReal);
        
        try {
            ReflectionTestUtils.invokeMethod(app, "analysisClassReference");
        } catch (RuntimeException ignore){}
        
        Assertions.assertEquals(updateCacheKey.size(), 20);
        Assertions.assertEquals(updateCacheKey.get(0), "com.godone.a.b.c");
        Assertions.assertEquals(updateCacheKey.get(1), "com.godone.a.d.f");
        
        // 因为带解析队列一直存在，所以会重复执行 10 次，得到 20 个待删除项
        Assertions.assertEquals(fileModels.size(), 20);
        Assertions.assertEquals(fileModels.get(0).getPackageName(), "com.godone.a.d");
        Assertions.assertEquals(fileModels.get(1).getPackageName(), "com.godone.a.d");
    }
    
    @Test
    @DisplayName("参数不完整")
    public void paramsError() {
        try {
            Application.main(new String[] {});
        } catch (Exception ignore){}
        
        Mockito.verify(mvnUtil, Mockito.times(0)).saveResource(Mockito.anyString(), Mockito.anyBoolean());
    }
    
    @Test
    @DisplayName("repository 默认值")
    public void repositoryDefault() {
        AtomicReference<String> repository = new AtomicReference<>("");
        File result = new File(TestUtil.getBaseDir()+ "com/godone/testSuite/oneapi.json");
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            repository.set((String) args[0]);
            return null;
        }).when(mvnUtil).saveReflectClassCache(Mockito.anyString());
        
        Mockito.when(entryCache.getCache()).thenReturn(new ArrayList<String>() {{
            add("com.test.a.b.c");
        }});
        Mockito.when(pendingCache.getCache()).thenReturn(new ArrayList<>());
        Mockito.when(entryAnalysis.analysis(Mockito.anyString())).thenReturn(new JavaFileModel());

        ReflectionTestUtils.invokeMethod(app, "run", (Object) new String[] {
            "-p",
            TestUtil.getBaseDir()+ "com/godone/testSuite",
            "-o",
            TestUtil.getBaseDir()+ "com/godone/testSuite",
        });

        Assertions.assertTrue(repository.get().contains(System.getProperty("user.home")));
        result.delete();
    }
    
    @Test
    @DisplayName("正常解析结果")
    public void analysisResult() {
        File result = new File(TestUtil.getBaseDir()+ "com/oneapi/spring/testSuite/oneapi.json");

        Application.main(new String[] {
            "-p",
            TestUtil.getBaseDir()+ "com/oneapi/spring/testSuite",
            "-o",
            TestUtil.getBaseDir()+ "com/oneapi/spring/testSuite",
            "-r",
            TestUtil.getBaseDir()+ "com/oneapi/spring/testSuite/field",
        });
//        try {
//
//        } catch (RuntimeException ignore){
//            System.out.print(ignore);
//        }
        
        Assertions.assertTrue(result.exists());
        result.delete();
    }
}