package com.godone.test.util.mavenUtil;

import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.cache.ResourceCache;
import com.etosun.godone.utils.MavenUtil;
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

@DisplayName("mavenUtil.getFuzzyImportPackage")
public class getFuzzyImportTest {
    @Mock
    private ReflectCache reflectCache;
    @Mock
    private ResourceCache resourceCache;
    @InjectMocks
    MavenUtil mvnUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("匹配同项目中导入的 class")
    public void matchResource() {
        Mockito.when(reflectCache.getCache()).thenReturn(new ArrayList<>());
        Mockito.when(resourceCache.getCache()).thenReturn(new ArrayList<String>(){{
            add("com.godone.models.ClassTypeEnum");
            add("com.godone.models.JavaActualType");
            add("com.godone.models.JavaAnnotationField");
            add("com.godone.util.FileUtils");
        }});
    
        List<String> importList = mvnUtil.getFuzzyImportPackage("com.godone.models.*");
        Assertions.assertEquals(importList, new ArrayList<String>(){{
            add("com.godone.models.ClassTypeEnum");
            add("com.godone.models.JavaActualType");
            add("com.godone.models.JavaAnnotationField");
        }});
    }
    
    @Test
    @DisplayName("匹配 jar 包中导入的 class")
    public void matchReflectClass() {
        Mockito.when(reflectCache.getCache()).thenReturn(new ArrayList<String>(){{
            add("com.ibm.icu.util.a");
            add("com.ibm.icu.util.b");
            add("com.ibm.icu.util.c");
            add("com.ibm.icu.utils.d");
        }});
        Mockito.when(resourceCache.getCache()).thenReturn(new ArrayList<>());
        
        List<String> importList = mvnUtil.getFuzzyImportPackage("com.ibm.icu.util.*");
        Assertions.assertEquals(importList, new ArrayList<String>(){{
            add("com.ibm.icu.util.a");
            add("com.ibm.icu.util.b");
            add("com.ibm.icu.util.c");
        }});
    }
}
