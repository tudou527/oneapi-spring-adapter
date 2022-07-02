package com.oneapi.spring.test.analysis.type;

import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.test.TestUtil;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@DisplayName("typeAnalysis.analysis")
public class BasicTypeTest {
    @Mock
    PendingCache pendingCache;
    @InjectMocks
    TypeAnalysis typeAnalysis;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    private JavaActualType getActualType(String fieldName) {
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setImports(new ArrayList<String>(){{
            add("java.util.HashMap");
            add("com.oneapi.spring.testSuite.Description");
            add("com.oneapi.spring.testSuite.AuthOperationEnum");
        }});

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.field.ComplexField");
        Assertions.assertNotNull(javaClass);
        return typeAnalysis.analysis(javaClass.getFieldByName(fieldName).getType(), fileModel);
    }
    
    @Test
    @DisplayName("java 内置类型")
    public void buildInType() {
        JavaActualType boolType = getActualType("biBool");
        Assertions.assertEquals(boolType.getName(), "boolean");
        Assertions.assertEquals(boolType.getClassPath(), "boolean");
        
        JavaActualType biByte = getActualType("biByte");
        Assertions.assertEquals(biByte.getName(), "byte");
        Assertions.assertEquals(biByte.getClassPath(), "byte");
        
        JavaActualType biShort = getActualType("biShort");
        Assertions.assertEquals(biShort.getName(), "short");
        Assertions.assertEquals(biShort.getClassPath(), "short");
        
        JavaActualType biInt = getActualType("biInt");
        Assertions.assertEquals(biInt.getName(), "int");
        Assertions.assertEquals(biInt.getClassPath(), "int");
        
        JavaActualType biLong = getActualType("biLong");
        Assertions.assertEquals(biLong.getName(), "long");
        Assertions.assertEquals(biLong.getClassPath(), "long");
        
        JavaActualType biFloat = getActualType("biFloat");
        Assertions.assertEquals(biFloat.getName(), "float");
        Assertions.assertEquals(biFloat.getClassPath(), "float");
        
        JavaActualType biDouble = getActualType("biDouble");
        Assertions.assertEquals(biDouble.getName(), "double");
        Assertions.assertEquals(biDouble.getClassPath(), "double");
        
        JavaActualType biChar = getActualType("biChar");
        Assertions.assertEquals(biChar.getName(), "char");
        Assertions.assertEquals(biChar.getClassPath(), "char");
    }
    
    @Test
    @DisplayName("简单类型")
    public void simpleType() {
        JavaActualType genericType = getActualType("genericField");
        Assertions.assertEquals(genericType.getName(), "T");
        Assertions.assertEquals(genericType.getClassPath(), "T");
        
        JavaActualType strType = getActualType("strField");
        Assertions.assertEquals(strType.getName(), "String");
        Assertions.assertEquals(strType.getClassPath(), "java.lang.String");
        
        JavaActualType boolType = getActualType("boolField");
        Assertions.assertEquals(boolType.getName(), "Boolean");
        Assertions.assertEquals(boolType.getClassPath(), "java.lang.Boolean");
    }
    
    @Test
    @DisplayName("子类")
    public void subClass() {
        List<String> pendCacheKey = new ArrayList<>();
        Mockito.doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            pendCacheKey.add((String) args[0]);
            return null;
        }).when(pendingCache).setCache(Mockito.anyString());
        
        JavaActualType subClass = getActualType("subClass");

        Assertions.assertNotNull(subClass);
        Assertions.assertEquals(pendCacheKey.size(),1);
        Assertions.assertEquals(pendCacheKey.get(0),"com.oneapi.spring.testSuite.field.ComplexField$ComplexSubClass");
    }
}
