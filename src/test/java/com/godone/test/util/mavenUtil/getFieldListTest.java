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
        String classPath = TestUtil.getBaseDir() + "com/godone/testSuite/icu4j-2.6.1.jar";
        Class<?> targetClass = mvnUtil.getMatchReflectClass( "com.ibm.icu.util.EasterRule", classPath);
        Assertions.assertNotNull(targetClass);
    
        List<Field> fieldList = mvnUtil.getFieldList(targetClass);
        Assertions.assertNotNull(fieldList);
        
        Assertions.assertEquals(fieldList.size(), 5);
        
        Assertions.assertEquals(fieldList.get(0).getType().getName(), "com.ibm.icu.util.GregorianCalendar");
        Assertions.assertEquals(fieldList.get(0).getName(), "gregorian");

        Assertions.assertEquals(fieldList.get(1).getType().getName(), "com.ibm.icu.util.GregorianCalendar");
        Assertions.assertEquals(fieldList.get(1).getName(), "orthodox");
        
        Assertions.assertEquals(fieldList.get(2).getType().getName(), "int");
        Assertions.assertEquals(fieldList.get(2).getName(), "daysAfterEaster");
        
        Assertions.assertEquals(fieldList.get(3).getType().getName(), "java.util.Date");
        Assertions.assertEquals(fieldList.get(3).getName(), "startDate");
        
        Assertions.assertEquals(fieldList.get(4).getType().getName(), "com.ibm.icu.util.GregorianCalendar");
        Assertions.assertEquals(fieldList.get(4).getName(), "calendar");
    }
    
}
