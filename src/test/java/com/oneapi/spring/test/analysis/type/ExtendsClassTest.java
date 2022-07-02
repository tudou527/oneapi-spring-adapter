package com.oneapi.spring.test.analysis.type;

import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.test.TestUtil;
import com.oneapi.spring.utils.Logger;
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

import java.util.ArrayList;

@DisplayName("typeAnalysis.analysis")
public class ExtendsClassTest {
    @Mock
    Logger log;
    @Mock
    private PendingCache pendingCache;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    private TypeAnalysis typeAnalysis;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        Mockito.doNothing().when(pendingCache).setCache(Mockito.any());
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }
    
    private JavaActualType getActualType(String fieldName) {
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setImports(new ArrayList<String>(){{
            add("java.util.HashMap");
            add("com.oneapi.spring.testSuite.Description");
            add("com.oneapi.spring.testSuite.AuthOperationEnum");
            add("com.oneapi.spring.testSuite.Result");
        }});

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.field.ComplexField");
        fileModel.setJavaSource(javaClass.getSource());
        Assertions.assertNotNull(javaClass);

        return typeAnalysis.analysis(javaClass.getFieldByName(fieldName).getType(), fileModel);
    }
    
    @Test
    @DisplayName("继承：? (extends|super) ImmutableCollection")
    public void basicGenericType() {
        JavaActualType extendField = getActualType("extendField");
        
        Assertions.assertNotNull(extendField);
        Assertions.assertEquals(extendField.getClassPath(), "com.oneapi.spring.testSuite.Result");
        Assertions.assertEquals(extendField.getItems().size(), 1);
        
        // 能解析到 extends 后的对象
        JavaActualType firstItem = extendField.getItems().get(0);
        Assertions.assertEquals(firstItem.getName(), "Description");
        Assertions.assertEquals(firstItem.getClassPath(), "com.oneapi.spring.testSuite.Description");
        Assertions.assertNull(firstItem.getItems());
    }
}
