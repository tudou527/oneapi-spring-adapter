package com.godone.test.analysis.base;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
import com.google.inject.Provider;
import com.thoughtworks.qdox.JavaProjectBuilder;
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

@DisplayName("basic.analysis")
public class NormalTest {
    @Mock
    FileUtil fileUtil;
    @Mock
    ClassUtil classUtil;
    @InjectMocks
    BasicAnalysis basicAnalysis;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getBuilder return null")
    public void getBuilderReturnNull() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenReturn(null);

        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.field.ComplexField");
    
        JavaFileModel javaModel = basicAnalysis.analysis(filePath);
        
        Assertions.assertNotNull(javaModel);
        Assertions.assertNull(javaModel.getFilePath());
        Assertions.assertNull(javaModel.getClassModel());
        Assertions.assertNull(javaModel.getDescription());
        Assertions.assertNull(javaModel.getPackageName());
    }
    
    @Test
    @DisplayName("no public class")
    public void noPublicClass() {
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenReturn(new JavaProjectBuilder());
    
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.field.ComplexField");
        
        JavaFileModel javaModel = basicAnalysis.analysis(filePath);
        
        Assertions.assertNotNull(javaModel);
        Assertions.assertNull(javaModel.getFilePath());
        Assertions.assertNull(javaModel.getClassModel());
        Assertions.assertNull(javaModel.getDescription());
        Assertions.assertNull(javaModel.getPackageName());
    }
    
    @Test
    @DisplayName("normal")
    public void normal() {
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.google.inject.Singleton");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.when(classUtil.getDescription(Mockito.any(), Mockito.any())).thenReturn(new JavaDescriptionModel());
        
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.field.ComplexField");
        
        JavaFileModel javaModel = basicAnalysis.analysis(filePath);
        
        Assertions.assertNotNull(javaModel);
        Assertions.assertEquals(javaModel.getFilePath(), filePath);
        Assertions.assertEquals(javaModel.getImports(), new ArrayList<String>(){{
            add("com.google.inject.Singleton");
        }});
        Assertions.assertEquals(javaModel.getPackageName(), "com.godone.testSuite.field");
    }
}
