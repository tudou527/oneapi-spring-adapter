package com.godone.test.util.classUtil;

import com.godone.meta.cache.ResourceCache;
import com.godone.meta.utils.ClassUtil;
import com.godone.meta.utils.MavenUtil;
import com.godone.test.TestUtil;
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
        String mockImport = "com.godone.testSuite.b";
        ArrayList<String> resourceCacheRes = new ArrayList<String>(){{
            add(mockImport);
        }};
        Mockito.when(resourceCache.getCache()).thenReturn(resourceCacheRes);
        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(TestUtil.getFileByClassPath(mockImport));
    
        ArrayList<String> mockImportPkg = new ArrayList<String>(){{
            add("com.godone.models.ClassTypeEnum");
            add("com.godone.models.JavaActualType");
            add("com.godone.models.JavaAnnotationField");
            add("com.godone.models.JavaAnnotationModel");
        }};
        Mockito.when(mvnUtil.getFuzzyImportPackage(Mockito.anyString())).thenReturn(mockImportPkg);

        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.TestController");
        List<String> importList = classUtil.getImports(javaClass);
        
        ArrayList<String> expectResult = new ArrayList<String>(){{
            add("com.godone.testSuite.field.FieldWithDefaultValue");
            add("com.google.inject.Singleton");
            add("com.godone.testSuite.b");
        }};

        Assertions.assertEquals(importList, expectResult);
    }
}
