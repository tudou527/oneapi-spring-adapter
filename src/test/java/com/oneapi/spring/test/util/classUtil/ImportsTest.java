package com.oneapi.spring.test.util.classUtil;

import com.oneapi.spring.cache.ResourceCache;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.utils.MavenUtil;
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

import java.util.ArrayList;
import java.util.List;

@DisplayName("classUtil.getImports")
public class ImportsTest {
    @Mock MavenUtil mvnUtil;
    @InjectMocks ClassUtil classUtil;
    @Mock ResourceCache resourceCache;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("返回所有导入对象")
    public void methodSingleDescription() {
        String mockImport = "com.oneapi.spring.testSuite.b";
        ArrayList<String> resourceCacheRes = new ArrayList<String>(){{
            add(mockImport);
        }};
        Mockito.when(resourceCache.getCache()).thenReturn(resourceCacheRes);
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(TestUtil.getFileByClassPath(mockImport));
    
        ArrayList<String> mockImportPkg = new ArrayList<String>(){{
            add("com.oneapi.spring.models.ClassTypeEnum");
            add("com.oneapi.spring.models.JavaActualType");
            add("com.oneapi.spring.models.JavaAnnotationField");
            add("com.oneapi.spring.models.JavaAnnotationModel");
        }};
        Mockito.when(mvnUtil.getFuzzyImportPackage(Mockito.anyString())).thenReturn(mockImportPkg);

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.TestController");
        List<String> importList = classUtil.getImports(javaClass);
        
        ArrayList<String> expectResult = new ArrayList<String>(){{
            add("com.oneapi.spring.testSuite.field.FieldWithDefaultValue");
            add("com.google.inject.Singleton");
            add("com.oneapi.spring.testSuite.b");
        }};

        Assertions.assertEquals(importList, expectResult);
    }
}
