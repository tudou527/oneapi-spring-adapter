package com.godone.test.analysis.reflect;

import com.etosun.godone.analysis.EntryAnalysis;
import com.etosun.godone.analysis.ReflectAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.models.*;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.godone.test.TestUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;

@DisplayName("reflectAnalysis.analysis")
public class ReflectAnalysisTest {
    @Mock
    MavenUtil mvnUtil;
    @Mock
    ReflectCache reflectCache;
    @Mock
    PendingCache pendingCache;
    @InjectMocks
    ReflectAnalysis reflectAnalysis;;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(mvnUtil.getFieldList(Mockito.any())).thenCallRealMethod();
        Mockito.doNothing().when(pendingCache).setCache(Mockito.any());
    }

    @Test
    @DisplayName("class not from jar file")
    public void notInJar() {
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(null);

        JavaFileModel javaModel = reflectAnalysis.analysis("com.google.inject.spi.ElementSource");
        Assertions.assertNull(javaModel);
    }
    
    @Test
    @DisplayName("class not found")
    public void classNotFound() {
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
        Mockito.when(mvnUtil.getMatchReflectClass(Mockito.any(), Mockito.any())).thenReturn(null);
        
        JavaFileModel javaModel = reflectAnalysis.analysis("com.google.inject.spi.ElementSource");
        Assertions.assertNotNull(javaModel);
        Assertions.assertNull(javaModel.getClassModel());
    }

    @Test
    @DisplayName("without source jar")
    public void normal() {
        Mockito.when(reflectCache.getCache(Mockito.any())).thenReturn(TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");
        Mockito.when(mvnUtil.getMatchReflectClass(Mockito.any(), Mockito.any())).thenCallRealMethod();
        
        JavaFileModel javaModel = reflectAnalysis.analysis("com.google.inject.spi.ElementSource");
        Assertions.assertNotNull(javaModel);
        Assertions.assertEquals(javaModel.getPackageName(), "com.google.inject.spi.ElementSource");
        Assertions.assertEquals(javaModel.getFilePath(), TestUtil.getBaseDir() + "com/godone/testSuite/guice-4.2.3.jar");

        JavaClassModel targetClass = javaModel.getClassModel();
        Assertions.assertNotNull(targetClass);
        Assertions.assertEquals(targetClass.getName(), "ElementSource");
        Assertions.assertEquals(targetClass.getClassPath(), "com.google.inject.spi.ElementSource");
        Assertions.assertFalse(targetClass.getIsPrivate());
        Assertions.assertTrue(targetClass.getIsPublic());
        Assertions.assertNull(targetClass.getSuperClass());
        
        ArrayList<JavaClassFieldModel> fieldList = targetClass.getFields();
        Assertions.assertEquals(fieldList.size(), 4);
    }
}
