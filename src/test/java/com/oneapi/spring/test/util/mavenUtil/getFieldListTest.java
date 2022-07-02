package com.oneapi.spring.test.util.mavenUtil;

import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.utils.MavenUtil;
import com.oneapi.spring.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

@DisplayName("mavenUtil.getFieldList")
public class getFieldListTest {
    @Mock
    Logger log;
    @InjectMocks
    MavenUtil mvnUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }
    
    @Test
    @DisplayName("返回字段及其类型")
    public void getFieldList() {
        String classPath = TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/guice-4.2.3.jar";
        Class<?> targetClass = mvnUtil.getMatchReflectClass( "com.google.inject.spi.ElementSource", classPath);
        Assertions.assertNotNull(targetClass);
    
        List<Field> fieldList = mvnUtil.getFieldList(targetClass);
        Assertions.assertNotNull(fieldList);
        
        Assertions.assertTrue(fieldList.size() > 0);
        
        Assertions.assertEquals(fieldList.get(0).getType().getName(), "com.google.inject.spi.ElementSource");
        Assertions.assertEquals(fieldList.get(0).getName(), "originalElementSource");
    
        Assertions.assertEquals(fieldList.get(1).getType().getName(), "boolean");
        Assertions.assertEquals(fieldList.get(1).getName(), "trustedOriginalElementSource");

        Assertions.assertEquals(fieldList.get(2).getType().getName(), "com.google.inject.spi.ModuleSource");
        Assertions.assertEquals(fieldList.get(2).getName(), "moduleSource");
        
        Assertions.assertEquals(fieldList.get(3).getType().getName(), "java.lang.Object");
        Assertions.assertEquals(fieldList.get(3).getName(), "declaringSource");
    
        Assertions.assertEquals(fieldList.get(4).getType().getName(), "com.google.inject.spi.ModuleAnnotatedMethodScanner");
        Assertions.assertEquals(fieldList.get(4).getName(), "scanner");
    }
    
}
