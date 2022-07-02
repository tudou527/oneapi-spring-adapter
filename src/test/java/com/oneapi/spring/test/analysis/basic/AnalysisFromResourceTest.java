package com.oneapi.spring.test.analysis.basic;

import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.ResourceCache;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaClassModel;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.test.TestUtil;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
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
            setClassPath("com.oneapi.spring.test.mockType");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("文件不存在")
    public void fileNotExist() {
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(null);
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.oneapi.spring.testSuite.PrivateClass");

        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("使用 qdox 解析失败")
    public void getBuilderFail() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenReturn(null);
    
        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.PrivateClass");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "");
        
        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("不存在 public 类")
    public void noPublicClass() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
    
        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.PrivateClass");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);
        
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.oneapi.spring.testSuite.PrivateClass");
        
        Assertions.assertNull(classModel);
    }
    
    
    @Test
    @DisplayName("子类")
    public void subClass() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.oneapi.spring.testSuite.field.targetClass");
        }});
    
        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.field.ComplexField");
        // Mock resourceCache.getCache 返回子类
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            String classPath = (String) args[0];
            if (classPath.contains("$")) {
                return null;
            }
            return filePath;
        }).when(resourceCache).getCache(Mockito.anyString());
    
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.oneapi.spring.testSuite.field.ComplexField$ComplexSubClass");
    
        Assertions.assertNotNull(classModel);
    
        JavaClassModel subClass = classModel.getClassModel();
        Assertions.assertEquals(subClass.getName(), "ComplexSubClass");
        Assertions.assertEquals(subClass.getClassPath(), "com.oneapi.spring.testSuite.field.ComplexField$ComplexSubClass");
        Assertions.assertEquals(subClass.getFields().size(), 1);
    }
    
    @Test
    @DisplayName("正常解析类型")
    public void normal() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.oneapi.spring.testSuite.field.targetClass");
        }});

        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.field.ComplexField");
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(filePath);

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "com.oneapi.spring.testSuite.field.ComplexField");

        Assertions.assertNotNull(classModel);
        Assertions.assertEquals(classModel.getFilePath(), filePath);
        Assertions.assertEquals(classModel.getImports(), new ArrayList<String>(){{
            add("com.oneapi.spring.testSuite.field.targetClass");
        }});
        Assertions.assertEquals(classModel.getPackageName(), "com.oneapi.spring.testSuite.field");
        
        Assertions.assertNotNull(classModel.getJavaSource());
        Assertions.assertNull(classModel.getDescription());
        
        Assertions.assertNotNull(classModel.getClassModel());
        Assertions.assertNotNull(classModel.getClassModel().getName(), "ComplexField");
        Assertions.assertNotNull(classModel.getClassModel().getClassPath(), "com.oneapi.spring.testSuite.field.ComplexField");
    }
}
