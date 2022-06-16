package com.godone.test.application;

import com.etosun.godone.Application;
import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.cache.FileModelCache;
import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.godone.test.TestUtil;
import com.google.inject.Provider;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Application")
public class ApplicationTest {
    @Mock
    MavenUtil mvnUtil;
    @Mock
    PendingCache pendingCache;
    @Mock
    FileModelCache fileModelCache;
    @Mock
    BasicAnalysis basicAnalysisReal;
    @Mock
    Provider<BasicAnalysis> basicAnalysis;
    @InjectMocks
    Application app;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("解析资源")
    public void analysisClassReferenceTest() {
        // mock 待解析的缓存队列
        ArrayList<String> mockPendingCacheList = new ArrayList<String>() {{
            add("com.godone.a.b.c");
            add("com.godone.a.d.f");
        }};
        List<String> removeCacheKey = new ArrayList<>();
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            removeCacheKey.add((String) args[0]);
            return null;
        }).when(pendingCache).removeCache(Mockito.any());
        Mockito.when(pendingCache.getCache()).thenReturn(mockPendingCacheList);

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
        
        ReflectionTestUtils.invokeMethod(app, "analysisClassReference");
        Assertions.assertEquals(removeCacheKey.size(), 20);
        Assertions.assertEquals(removeCacheKey.get(0), "com.godone.a.b.c");
        Assertions.assertEquals(removeCacheKey.get(1), "com.godone.a.d.f");
        
        // 因为带解析队列一直存在，所以会重复执行 10 次，得到 20 个待删除项
        Assertions.assertEquals(fileModels.size(), 20);
        Assertions.assertEquals(fileModels.get(0).getPackageName(), "com.godone.a.d");
        Assertions.assertEquals(fileModels.get(1).getPackageName(), "com.godone.a.d");
    }
    
    @Test
    @DisplayName("参数不完整")
    public void paramsError() {
        Application.main(new String[] {});
        
        Mockito.verify(mvnUtil, Mockito.times(0)).saveResource(Mockito.anyString(), Mockito.anyBoolean());
    }
    
    @Test
    @DisplayName("有完整的解析结果")
    public void analysisResult() {
        File result = new File(TestUtil.getBaseDir()+ "com/godone/testSuite/result.json");

        Application.main(new String[] {
            "-p",
            TestUtil.getBaseDir()+ "com/godone/testSuite",
            "-o",
            TestUtil.getBaseDir()+ "com/godone/testSuite",
            "-r",
            TestUtil.getBaseDir()+ "com/godone/testSuite/field",
        });
        
        Assertions.assertTrue(result.exists());
        result.delete();
    }
}
