package com.godone.test.analysis.basic;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaClassFieldModel;
import com.etosun.godone.models.JavaClassModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;

@DisplayName("basic.analysis.analysisFromResource")
public class AnalysisFromResourceTest {
    @Mock
    FileUtil fileUtil;
    @Mock
    ClassUtil classUtil;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenReturn(new JavaActualType(){{
            setName("mockType");
            setClassPath("com.godone.test.mockType");
        }});
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
    }

    @Test
    @DisplayName("文件不存在")
    public void fileNotExist() {
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", new Object[]{ null });

        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("使用 qdox 解析失败")
    public void getBuilderFail() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenReturn(null);

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", "");
        
        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("不存在 public 类")
    public void noPublicClass() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.PrivateClass");
        
        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", filePath);
        
        Assertions.assertNull(classModel);
    }
    
    @Test
    @DisplayName("正常解析类型")
    public void normal() {
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.godone.testSuite.field.targetClass");
        }});

        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.field.ComplexField");

        JavaFileModel classModel = ReflectionTestUtils.invokeMethod(basicAnalysis, "analysisFromResource", filePath);

        Assertions.assertNotNull(classModel);
        Assertions.assertEquals(classModel.getFilePath(), filePath);
        Assertions.assertEquals(classModel.getImports(), new ArrayList<String>(){{
            add("com.godone.testSuite.field.targetClass");
        }});
        Assertions.assertEquals(classModel.getPackageName(), "com.godone.testSuite.field");
        
        Assertions.assertNotNull(classModel.getJavaSource());
        Assertions.assertNull(classModel.getDescription());
        
        Assertions.assertNotNull(classModel.getClassModel());
        Assertions.assertNotNull(classModel.getClassModel().getName(), "ComplexField");
        Assertions.assertNotNull(classModel.getClassModel().getClassPath(), "com.godone.testSuite.field.ComplexField");
    }
}
