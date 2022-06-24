package com.godone.test.analysis.basic;

import com.godone.meta.analysis.BasicAnalysis;
import com.godone.meta.analysis.TypeAnalysis;
import com.godone.meta.cache.ResourceCache;
import com.godone.meta.models.JavaActualType;
import com.godone.meta.models.JavaFileModel;
import com.godone.meta.utils.ClassUtil;
import com.godone.meta.utils.FileUtil;
import com.godone.meta.utils.Logger;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;

@DisplayName("basic.analysis.analysisFromResource")
public class AnalysisFromResourceTest {
    @Mock
    Logger log;
    @Mock
    FileUtil fileUtil;
    @Mock
    ClassUtil classUtil;
    @Mock
    ResourceCache resourceCache;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenReturn(new JavaActualType(){{
            setName("mockType");
            setClassPath("com.godone.test.mockType");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("文件不存在")
    public void fileNotExist() {
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(null);
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.godone.testSuite.PrivateClass");

        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("使用 qdox 解析失败")
    public void getBuilderFail() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenReturn(null);
    
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.PrivateClass");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "");
        
        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("不存在 public 类")
    public void noPublicClass() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
    
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.PrivateClass");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);
        
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.godone.testSuite.PrivateClass");
        
        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("正常解析类型")
    public void normal() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.godone.testSuite.field.targetClass");
        }});

        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.field.ComplexField");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.godone.testSuite.field.ComplexField");

        Assertions.assertNotNull(classModel);
        Assertions.assertEquals(classModel.getFilePath(), filePath);
        Assertions.assertEquals(classModel.getImports(), new ArrayList<String>(){{
            add("com.godone.testSuite.field.targetClass");
        }});
        Assertions.assertEquals(classModel.getPackageName(), "com.godone.testSuite.field");
        
        Assertions.assertNotNull(classModel.getJavaSource());
        Assertions.assertNull(classModel.getDescription());
        
        Assertions.assertNotNull(classModel.getClassModel());
        Assertions.assertNotNull(classModel.getClassModel().getName(), "ComplexField");
        Assertions.assertNotNull(classModel.getClassModel().getClassPath(), "com.godone.testSuite.field.ComplexField");
    }
}
