package com.godone.test.analysis.type;

import com.godone.meta.analysis.TypeAnalysis;
import com.godone.meta.models.JavaActualType;
import com.godone.meta.models.JavaFileModel;
import com.godone.test.TestUtil;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@DisplayName("typeAnalysis.analysis")
public class BasicTypeTest {
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
            add("com.godone.testSuite.Description");
            add("com.godone.testSuite.AuthOperationEnum");
        }});

        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.field.ComplexField");
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
}
