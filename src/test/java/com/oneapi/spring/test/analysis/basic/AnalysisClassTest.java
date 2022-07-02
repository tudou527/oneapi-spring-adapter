package com.oneapi.spring.test.analysis.basic;

import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.models.JavaClassFieldModel;
import com.oneapi.spring.models.JavaClassModel;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.test.TestUtil;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

@DisplayName("basic.analysis.analysisClass")
public class AnalysisClassTest {
    @Mock
    Logger log;
    @Mock
    ClassUtil classUtil;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
        
        Mockito.when(classUtil.getActualTypeParameters(Mockito.any())).thenCallRealMethod();
        Mockito.when(classUtil.getDescription(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(classUtil.getAnnotation(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
        
        // 设置 private 属性
        ReflectionTestUtils.setField(basicAnalysis, "fileLines", new ArrayList<String>());
        ReflectionTestUtils.setField(basicAnalysis, "fileModel", new JavaFileModel(){{
            setImports(new ArrayList<String>(){{
                add("com.google.inject.Singleton");
            }});
        }});
    }

    @Test
    @DisplayName("解析 class 基础信息")
    public void normal() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.Description");
    
        JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
        
        Assertions.assertNotNull(classModel);
        
        Assertions.assertEquals(classModel.getName(), "Description");
        Assertions.assertEquals(classModel.getClassPath(), "com.oneapi.spring.testSuite.Description");
        Assertions.assertEquals(classModel.getActualType().size(), 2);
        Assertions.assertNull(classModel.getSuperClass());

        // getDescription、getAnnotation 的单测已经写过了，这里简单的判断
        Assertions.assertNotNull(classModel.getDescription());
        Assertions.assertEquals(classModel.getAnnotations().size(), 0);
        
        Assertions.assertFalse(classModel.getIsEnum());
        Assertions.assertFalse(classModel.getIsPrivate());
        Assertions.assertTrue(classModel.getIsPublic());
        Assertions.assertFalse(classModel.getIsAbstract());
        Assertions.assertFalse(classModel.getIsInterface());

        ArrayList<JavaClassFieldModel> fields = classModel.getFields();
        Assertions.assertEquals(fields.size(), 2);
    }

    @Test
    @DisplayName("枚举类")
    public void enumClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.AuthOperationEnum");
    
        JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
    
        Assertions.assertNotNull(classModel);
        Assertions.assertTrue(classModel.getIsEnum());
    }
    
    @Test
    @DisplayName("接口类")
    public void interfaceClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.UserInterface");
        
        JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
        
        Assertions.assertNotNull(classModel);
        Assertions.assertTrue(classModel.getIsInterface());
    }
}
