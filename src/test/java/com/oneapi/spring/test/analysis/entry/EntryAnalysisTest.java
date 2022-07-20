package com.oneapi.spring.test.analysis.entry;

import com.oneapi.spring.analysis.EntryAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.ResourceCache;
import com.oneapi.spring.models.*;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.test.TestUtil;
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

@DisplayName("entryAnalysis.analysis")
public class EntryAnalysisTest {
    @Mock
    Logger log;
    @Mock
    FileUtil fileUtil;
    @Mock
    ClassUtil classUtil;
    @Mock
    TypeAnalysis typeAnalysisReal;
    @Mock
    ResourceCache resourceCache;
    @Mock
    Provider<TypeAnalysis> typeAnalysis;
    @InjectMocks
    EntryAnalysis entryAnalysis;;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
        Mockito.when(fileUtil.getFileOrIOEncode(Mockito.any())).thenReturn(Charset.defaultCharset());
        Mockito.when(fileUtil.getBuilder(Mockito.any())).thenCallRealMethod();
        Mockito.when(classUtil.getImports(Mockito.any())).thenReturn(new ArrayList<String>(){{
            add("com.google.inject.Singleton");
            add("import com.oneapi.spring.testSuite.field.FieldWithDefaultValue");
        }});
        Mockito.when(classUtil.getDescription(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(classUtil.getAnnotation(Mockito.any(), Mockito.any())).thenReturn(new ArrayList());
    
        // mock 参数及返回值类型
        Mockito.when(typeAnalysisReal.analysis(Mockito.any(), Mockito.any())).thenReturn(new JavaActualType(){{
            setName("CustomJavaType");
        }});
        Mockito.when(typeAnalysis.get()).thenReturn(typeAnalysisReal);

        Mockito.when(resourceCache.getCache(Mockito.anyString())).thenReturn(TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.TestController"));
    }

    @Test
    @DisplayName("normal")
    public void normal() {
        JavaFileModel javaModel = entryAnalysis.analysis("com.oneapi.spring.testSuite.TestController");
        Assertions.assertNotNull(javaModel);
        
        // 存在 public method
        ArrayList<JavaClassMethodModel> methodList = javaModel.getClassModel().getMethods();
        Assertions.assertTrue(methodList.size() > 0);
    
        JavaClassMethodModel contentTypeXml = methodList.get(0);
        Assertions.assertNotNull(contentTypeXml);
        Assertions.assertEquals(contentTypeXml.getName(), "contentTypeXml");
        
        // 描述
        JavaDescriptionModel methodDesc = contentTypeXml.getDescription();
        Assertions.assertNotNull(methodDesc);
        Assertions.assertEquals(methodDesc.getText(), "方法 contentTypeXml\nparams argsA 参数 A");
        Assertions.assertEquals(methodDesc.getTag().size(), 1);
        
        // 跳过注解断言（在注解用例中已经测试过）
        ArrayList<JavaAnnotationModel> methodAn = contentTypeXml.getAnnotations();
        Assertions.assertTrue(methodAn.size() == 0);
        
        // 入参
        ArrayList<JavaMethodParameter> params = contentTypeXml.getParameters();
        Assertions.assertEquals(params.size(), 1);
        Assertions.assertEquals(params.get(0).getName(), "argsA");
        Assertions.assertEquals(params.get(0).getType().getName(), "CustomJavaType");
        // 参数描述及注解
        Assertions.assertNull(params.get(0).getDescription().getText());
        Assertions.assertTrue(params.get(0).getAnnotations().size() == 0);
        
        // 返回值
        JavaActualType returnType = contentTypeXml.getReturnType();
        Assertions.assertEquals(returnType.getName(), "CustomJavaType");
    }
    
    @Test
    @DisplayName("no params")
    public void noArguments() {
        JavaFileModel javaModel = entryAnalysis.analysis("com.oneapi.spring.testSuite.TestController");
        Assertions.assertNotNull(javaModel);
        
        // 存在 public method
        ArrayList<JavaClassMethodModel> methodList = javaModel.getClassModel().getMethods();
        Assertions.assertTrue(methodList.size() > 0);
    
        Optional<JavaClassMethodModel> voidMethod = methodList.stream().filter(m -> m.getName().equals("noArguments")).findAny();
        Assertions.assertTrue(voidMethod.isPresent());
        Assertions.assertEquals(voidMethod.get().getParameters().size(), 0);
    }
}
