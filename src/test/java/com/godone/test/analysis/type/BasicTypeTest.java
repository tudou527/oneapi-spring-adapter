package com.godone.test.analysis.type;

import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
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
    @DisplayName("basic type")
    public void noParentClass() {
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
