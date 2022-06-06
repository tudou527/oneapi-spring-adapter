package com.godone.test.analysis.type;

import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
import com.godone.test.TestUtil;
import com.google.inject.Inject;
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
public class ComplexTypeTest {
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
    }
    
    private JavaActualType getActualType(String fieldName) {
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setImports(new ArrayList<String>(){{
            add("java.util.HashMap");
            add("com.godone.testSuite.Description");
            add("com.godone.testSuite.AuthOperationEnum");
        }});

        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.field.ComplexField");
        Assertions.assertNotNull(javaClass);
        return typeAnalysis.analysis(javaClass.getFieldByName(fieldName).getType(), fileModel);
    }
    
    @Test
    @DisplayName("basic generic type")
    public void basicGenericType() {
        JavaActualType genericType = getActualType("genericProperty");
        Assertions.assertEquals(genericType.getName(),  "Description");
        Assertions.assertEquals(genericType.getClassPath(), "com.godone.testSuite.Description");
        
        ArrayList<JavaActualType> genItem = genericType.getItem();
        Assertions.assertEquals(genItem.size(), 2);
        Assertions.assertEquals(genItem.get(0).getName(), "T");
        Assertions.assertEquals(genItem.get(0).getClassPath(), "T");
        Assertions.assertEquals(genItem.get(1).getName(), "T");
        Assertions.assertEquals(genItem.get(1).getClassPath(), "T");
    }
    
    @Test
    @DisplayName("generic type in list")
    public void listGenericType() {
        JavaActualType genericType = getActualType("complexGenericProperty1");
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");

        // TODO: 判断子类型
    }
    
}
