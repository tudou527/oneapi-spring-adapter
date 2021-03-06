package com.oneapi.spring.test.analysis.basic;

import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaFileModel;
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

@DisplayName("basic.analysis.getParentClass")
public class GetParentClassTest {
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        // 设置 private 属性
        ReflectionTestUtils.setField(basicAnalysis, "fileModel", new JavaFileModel(){{
            setImports(new ArrayList<String>(){{
                add("com.google.inject.Singleton");
            }});
        }});
    }

    @Test
    @DisplayName("不存在父类")
    public void noParentClass() {
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.field.ComplexField");

        JavaActualType parentClass = ReflectionTestUtils.invokeMethod(basicAnalysis, "getParentClass", javaClass);

        Assertions.assertNull(parentClass);
    }
    
    @Test
    @DisplayName("父类为泛型")
    public void genericParent() {
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenReturn(new JavaActualType() {{
            setName("testJavaActualTypeName");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.ExtendGenericClazz");
        
        JavaActualType parentClass = ReflectionTestUtils.invokeMethod(basicAnalysis, "getParentClass", javaClass);
        Assertions.assertNotNull(parentClass);
        Assertions.assertEquals(parentClass.getName(), "testJavaActualTypeName");
    }
}
