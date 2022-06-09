package com.godone.test.analysis.basic;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
import com.godone.test.TestUtil;
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

@DisplayName("basic.getParentClass")
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
    @DisplayName("no parent class")
    public void noParentClass() {
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);

        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.field.ComplexField");

        try {
            JavaActualType parentClass = ReflectionTestUtils.invokeMethod(basicAnalysis, "getParentClass", javaClass);

            Assertions.assertNull(parentClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("generic parent class")
    public void genericParent() {
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenReturn(new JavaActualType() {{
            setName("testJavaActualTypeName");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.ExtendGenericClazz");
        
        try {
            JavaActualType parentClass = ReflectionTestUtils.invokeMethod(basicAnalysis, "getParentClass", javaClass);
            Assertions.assertNotNull(parentClass);
            Assertions.assertEquals(parentClass.getName(), "testJavaActualTypeName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
