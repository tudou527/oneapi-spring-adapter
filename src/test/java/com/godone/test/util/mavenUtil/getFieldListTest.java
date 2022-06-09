package com.godone.test.util.mavenUtil;

import com.etosun.godone.utils.MavenUtil;
import com.godone.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

@DisplayName("mavenUtil.getFieldList")
public class getFieldListTest {
    @InjectMocks
    MavenUtil mvnUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("normal")
    public void getFieldList() {
        String classPath = TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar";
        Class<?> targetClass = mvnUtil.getMatchReflectClass( "com.google.inject.spi.ElementSource", classPath);
        Assertions.assertNotNull(targetClass);
    
        List<Field> fieldList = mvnUtil.getFieldList(targetClass);
        Assertions.assertNotNull(fieldList);
        
        Assertions.assertEquals(fieldList.size(), 4);
        
        Assertions.assertEquals(fieldList.get(0).getType().getName(), "com.google.inject.spi.ElementSource");
        Assertions.assertEquals(fieldList.get(0).getName(), "originalElementSource");

        Assertions.assertEquals(fieldList.get(1).getType().getName(), "com.google.inject.spi.ModuleSource");
        Assertions.assertEquals(fieldList.get(1).getName(), "moduleSource");
        
        // TODO: 需要考虑类型为子类的情况
        Assertions.assertEquals(fieldList.get(2).getType().getName(), "[Lcom.google.inject.internal.util.StackTraceElements$InMemoryStackTraceElement;");
        Assertions.assertEquals(fieldList.get(2).getName(), "partialCallStack");
        
        Assertions.assertEquals(fieldList.get(3).getType().getName(), "java.lang.Object");
        Assertions.assertEquals(fieldList.get(3).getName(), "declaringSource");
    }
    
}
