package com.oneapi.spring.test.util.classUtil;

import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.test.TestUtil;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@DisplayName("classUtil.getActualTypeParameters")
public class ClassActualTypeTest {
    @InjectMocks ClassUtil classUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("泛型 class")
    public void actualClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.Description");
    
        ArrayList<JavaActualType> actualType = classUtil.getActualTypeParameters(javaClass);
    
        Assertions.assertEquals(actualType.size(), 2);
    
        Assertions.assertEquals(actualType.get(0).getName(), "T");
        Assertions.assertEquals(actualType.get(1).getName(), "U");
    }
    
    @Test
    @DisplayName("无泛型")
    public void normalClass() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.TestController");
        Assertions.assertNull(classUtil.getActualTypeParameters(javaClass));
    }
}
