package com.godone.test.util.classUtil;

import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.JavaAnnotationField;
import com.etosun.godone.models.JavaAnnotationModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.ClassUtil;
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

@DisplayName("classUtil.getAnnotation")
public class AnnotationTest {
    @Mock PendingCache pendingCache;
    @InjectMocks ClassUtil classUtil;

    JavaFileModel mockFileModel = Mockito.mock(JavaFileModel.class);

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        Mockito.when(mockFileModel.getImports()).thenReturn(new ArrayList<String>(){{
            add("com.godone.testSuite.CustomAn");
            add("com.google.inject.Singleton");
            add("com.godone.testSuite.AuthOperationEnum");
        }});
    }
    
    @Test
    @DisplayName("正常注解属性")
    public void classAnnotation() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.TestController");
    
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
        Assertions.assertEquals(wiredAn.getClassPath(), "com.godone.testSuite.CustomAn");
        // 注解字段
        ArrayList<JavaAnnotationField> fields = wiredAn.getFields();
        Assertions.assertEquals(fields.size(), 5);

        Assertions.assertEquals(fields.get(0).getName(), "version");
        Assertions.assertEquals(fields.get(0).getType(), "com.godone.testSuite.AuthOperationEnum");
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
    @DisplayName("注解入参为 null")
    public void annotationNull() {
        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(null, mockFileModel);
        Assertions.assertNull(annotations);
    }
    
    @Test
    @DisplayName("注解不存在")
    public void annotationEmpty() {
        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(new ArrayList<>(), mockFileModel);
        Assertions.assertNull(annotations);
    }
}
