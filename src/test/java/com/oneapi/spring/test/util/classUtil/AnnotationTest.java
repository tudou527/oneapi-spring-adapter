package com.oneapi.spring.test.util.classUtil;

import com.google.inject.Provider;
import com.oneapi.spring.analysis.EntryAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.models.JavaAnnotationField;
import com.oneapi.spring.models.JavaAnnotationModel;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.test.TestUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

@DisplayName("classUtil.getAnnotation")
public class AnnotationTest {
    @Mock
    PendingCache pendingCache;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    ClassUtil classUtil;

    JavaFileModel mockFileModel = Mockito.mock(JavaFileModel.class);

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        Mockito.when(mockFileModel.getImports()).thenReturn(new ArrayList<String>(){{
            add("com.oneapi.spring.testSuite.CustomAn");
            add("com.google.inject.Singleton");
            add("com.oneapi.spring.testSuite.AuthOperationEnum");
            add("com.oneapi.spring.testSuite.CustomController");
        }});
    }
    
    @Test
    @DisplayName("解析注解属性")
    public void classAnnotation() {
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.TestController");

        // Mock pendingCache.setCache 方法
        Mockito.doNothing().when(pendingCache).setCache(Mockito.anyString());

        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(javaClass.getAnnotations(), mockFileModel);

        // 解析到 3 个注解
        Assertions.assertEquals(annotations.size() , 3);

        // @Singleton 注解
        JavaAnnotationModel restAnnotation = annotations.get(0);
        Assertions.assertEquals(restAnnotation.getName(), "Singleton");
        Assertions.assertEquals(restAnnotation.getClassPath(), "com.google.inject.Singleton");
        Assertions.assertEquals(restAnnotation.getFields().size(), 0);
        
        // CustomAn 注解
        JavaAnnotationModel wiredAn = annotations.get(1);
        Assertions.assertEquals(wiredAn.getName(), "CustomAn");
        Assertions.assertEquals(wiredAn.getClassPath(), "com.oneapi.spring.testSuite.CustomAn");
        // 注解字段
        ArrayList<JavaAnnotationField> fields = wiredAn.getFields();
        Assertions.assertEquals(fields.size(), 5);

        Assertions.assertEquals(fields.get(0).getName(), "version");
        Assertions.assertEquals(fields.get(0).getType(), "com.oneapi.spring.testSuite.AuthOperationEnum");
        Assertions.assertTrue(fields.get(0).isArray());
        Assertions.assertEquals(fields.get(0).getValue(), new ArrayList<String>(){{
            add("AuthOperationEnum.ARTICLE_SEARCH");
        }});
    
        Assertions.assertEquals(fields.get(1).getName(), "value");
        Assertions.assertEquals(fields.get(1).getType(), "Constant");
        Assertions.assertFalse(fields.get(1).isArray());
        Assertions.assertEquals(fields.get(1).getValue(), "anValue");
    
        Assertions.assertEquals(fields.get(2).getName(), "required");
        Assertions.assertEquals(fields.get(2).getType(), "Constant");
        Assertions.assertFalse(fields.get(2).isArray());
        Assertions.assertEquals(fields.get(2).getValue(), false);
    
        Assertions.assertEquals(fields.get(3).getName(), "index");
        Assertions.assertEquals(fields.get(3).getType(), "Constant");
        Assertions.assertFalse(fields.get(3).isArray());
        Assertions.assertEquals(fields.get(3).getValue(), 2);
    
        Assertions.assertEquals(fields.get(4).getName(), "name");
        Assertions.assertEquals(fields.get(4).getType(), "Constant");
        Assertions.assertTrue(fields.get(4).isArray());
        Assertions.assertEquals(fields.get(4).getValue(), new ArrayList<String>(){{
            add("a");
            add("b");
            add("c");
        }});
    }
    
    @Test
    @DisplayName("入参为 null")
    public void annotationNull() {
        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(null, mockFileModel);
        Assertions.assertEquals(annotations.size(), 0);
    }
    
    @Test
    @DisplayName("入参为空数组")
    public void annotationEmpty() {
        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(new ArrayList<>(), mockFileModel);
        Assertions.assertEquals(annotations.size(), 0);
    }

    @Test
    @DisplayName("属性为 class")
    public void attrIsClass() {
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenCallRealMethod();

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.TestController");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(m -> m.getName().contains("attrIsClassInAnnotation")).findAny();

        Assertions.assertTrue(method.isPresent());

        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(method.get().getAnnotations(), mockFileModel);
        Assertions.assertNotNull(annotations);
        Assertions.assertEquals(annotations.size(), 1);

        JavaAnnotationModel firstAn = annotations.get(0);
        Assertions.assertEquals(firstAn.getName(), "CustomClassAn");

        ArrayList<JavaAnnotationField> fields = firstAn.getFields();
        Assertions.assertEquals(fields.size(), 1);
        Assertions.assertEquals(fields.get(0).getName(), "level");
    }

    @Test
    @DisplayName("annotation value type is DefaultJavaAnnotation")
    public void defaultJavaAnnoValueType() {
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
        Mockito.when(typeAnalysis.analysis(Mockito.any(), Mockito.any())).thenCallRealMethod();

        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.TestController");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(m -> m.getName().contains("defaultJavaAnnoValueType")).findAny();

        Assertions.assertTrue(method.isPresent());
        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(method.get().getAnnotations(), mockFileModel);
        Assertions.assertNotNull(annotations);

        ArrayList<JavaAnnotationField> anFields = annotations.get(0).getFields();
        Assertions.assertEquals(anFields.size(), 1);

        JavaAnnotationField firstField = anFields.get(0);
        Assertions.assertEquals(firstField.getName(), "value");
        Assertions.assertEquals(firstField.isArray(), true);

        ArrayList<JavaAnnotationModel> values = (ArrayList) firstField.getValue();
        Assertions.assertEquals(values.size(), 1);

        JavaAnnotationModel firstChild = values.get(0);
        Assertions.assertEquals(firstChild.getName(), "CustomClassAn");
        Assertions.assertEquals(firstChild.getFields().get(0).getName(), "level");
    }
}
