package com.godone.test.analysis.base;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaClassFieldModel;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.ClassUtil;
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

@DisplayName("basic.analysis")
public class GetFieldListTest {
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
    
    private JavaClassFieldModel getField(ArrayList<JavaClassFieldModel> fieldList, String fieldName) {
        return fieldList.stream().filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
    }

    @Test
    @DisplayName("normal")
    public void normal() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.field.ComplexField");

        try {
            ArrayList<JavaClassFieldModel> fields = ReflectionTestUtils.invokeMethod(basicAnalysis, "getFieldList", javaClass);

            Assertions.assertNotNull(fields);
    
            JavaClassFieldModel result2 = getField(fields, "result2");
            Assertions.assertNotNull(result2);
            Assertions.assertEquals(result2.getName(), "result2");
            Assertions.assertEquals(result2.getDefaultValue(), "");

            Assertions.assertTrue(result2.getIsPrivate());
            Assertions.assertFalse(result2.getIsPublic());
            Assertions.assertFalse(result2.getIsProtected());
            
            Assertions.assertTrue(fields.size() > 1);
    
            JavaClassFieldModel errorMsg = getField(fields, "errorMsg");
            Assertions.assertNotNull(errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
