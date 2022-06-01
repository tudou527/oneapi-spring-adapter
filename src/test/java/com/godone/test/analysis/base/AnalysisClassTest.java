package com.godone.test.analysis.base;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.*;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@DisplayName("basic.analysis")
public class AnalysisClassTest {
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
        
        // 设置 private 属性
        ReflectionTestUtils.setField(basicAnalysis, "fileLines", new ArrayList<String>());
        ReflectionTestUtils.setField(basicAnalysis, "fileModel", new JavaFileModel(){{
            setImports(new ArrayList<String>(){{
                add("com.google.inject.Singleton");
            }});
        }});
    }

    @Test
    @DisplayName("normal")
    public void normal() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.Description");
        
        try {
            JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
            
            Assertions.assertNotNull(classModel);
            
            Assertions.assertEquals(classModel.getName(), "Description");
            Assertions.assertEquals(classModel.getClassPath(), "com.godone.testSuite.Description");
            Assertions.assertEquals(classModel.getActualType().size(), 2);
            Assertions.assertNull(classModel.getSuperClass());

            // getDescription、getAnnotation 的单测已经写过了，这里简单的判断
            Assertions.assertNotNull(classModel.getDescription());
            Assertions.assertNull(classModel.getAnnotation());
            
            Assertions.assertFalse(classModel.getIsEnum());
            Assertions.assertFalse(classModel.getIsPrivate());
            Assertions.assertTrue(classModel.getIsPublic());
            Assertions.assertFalse(classModel.getIsAbstract());
            Assertions.assertFalse(classModel.getIsInterface());
    
            ArrayList<JavaClassFieldModel> fields = classModel.getFields();
            Assertions.assertEquals(fields.size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("enum class")
    public void enumClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.AuthOperationEnum");
    
        try {
            JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
        
            Assertions.assertNotNull(classModel);
            Assertions.assertTrue(classModel.getIsEnum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("interface")
    public void interfaceClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.UserInterface");
        
        try {
            JavaClassModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisClass", javaClass);
            
            Assertions.assertNotNull(classModel);
            Assertions.assertTrue(classModel.getIsInterface());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
