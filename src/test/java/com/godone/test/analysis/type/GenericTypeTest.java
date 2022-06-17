package com.godone.test.analysis.type;

import com.godone.meta.analysis.TypeAnalysis;
import com.godone.meta.cache.PendingCache;
import com.godone.meta.models.JavaActualType;
import com.godone.meta.models.JavaFileModel;
import com.godone.test.TestUtil;
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

import java.util.ArrayList;

@DisplayName("typeAnalysis.analysis")
public class GenericTypeTest {
    @Mock
    private PendingCache pendingCache;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    private TypeAnalysis typeAnalysis;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        Mockito.doNothing().when(pendingCache).setCache(Mockito.any());
        Mockito.when(typeAnalysisProvider.get()).thenReturn(typeAnalysis);
    }
    
    private JavaActualType getActualType(String fieldName) {
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setImports(new ArrayList<String>(){{
            add("java.util.HashMap");
            add("com.godone.testSuite.Description");
            add("com.godone.testSuite.AuthOperationEnum");
        }});

        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.field.ComplexField");
        fileModel.setJavaSource(javaClass.getSource());
    
        Assertions.assertNotNull(javaClass);

        return typeAnalysis.analysis(javaClass.getFieldByName(fieldName).getType(), fileModel);
    }
    
    @Test
    @DisplayName("简单泛型: CustomClass<T, T>")
    public void basicGenericType() {
        JavaActualType genericType = getActualType("genericProperty");
        Assertions.assertEquals(genericType.getName(),  "Description");
        Assertions.assertEquals(genericType.getClassPath(), "com.godone.testSuite.Description");
        
        ArrayList<JavaActualType> genItem = genericType.getItem();
        Assertions.assertEquals(genItem.size(), 2);
        Assertions.assertEquals(genItem.get(0).getName(), "T");
        Assertions.assertEquals(genItem.get(0).getClassPath(), "T");
        Assertions.assertEquals(genItem.get(1).getName(), "T");
        Assertions.assertEquals(genItem.get(1).getClassPath(), "T");
    }
    
    @Test
    @DisplayName("数组嵌套的泛型: List<CustomClass<String, CustomClass<T, T>>>")
    public void listGenericType() {
        JavaActualType genericType = getActualType("complexGenericProperty1");
        
        // 最外层是 java.util.List
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");
        // 包含 1 个子节点
        Assertions.assertEquals(genericType.getItem().size(), 1);
        
        JavaActualType firstItem = genericType.getItem().get(0);
        // 子节点为 Description<>
        Assertions.assertEquals(firstItem.getName(), "Description");
        Assertions.assertEquals(firstItem.getClassPath(), "com.godone.testSuite.Description");
        // 包含 2 个节点
        Assertions.assertEquals(firstItem.getItem().size(), 2);
        
        // 第一个节点是 String
        Assertions.assertEquals(firstItem.getItem().get(0).getName(), "String");
        Assertions.assertEquals(firstItem.getItem().get(0).getClassPath(), "java.lang.String");
        Assertions.assertNull(firstItem.getItem().get(0).getItem());
        
        // 第二个节点依然是 Description
        Assertions.assertEquals(firstItem.getItem().get(1).getName(), "Description");
        Assertions.assertEquals(firstItem.getItem().get(1).getClassPath(), "com.godone.testSuite.Description");
        // 因为 Description 是泛型，所以依然有 2 个节点
        Assertions.assertEquals(firstItem.getItem().get(1).getItem().size(), 2);
        
        // 每个节点的子类型都是 T
        ArrayList<JavaActualType> childItem = firstItem.getItem().get(1).getItem();
        Assertions.assertEquals(childItem.size(), 2);
        Assertions.assertEquals(childItem.get(0).getName(), "T");
        Assertions.assertEquals(childItem.get(0).getClassPath(), "T");
        Assertions.assertNull(childItem.get(0).getItem());
    
        Assertions.assertEquals(childItem.get(1).getName(), "T");
        Assertions.assertEquals(childItem.get(1).getClassPath(), "T");
        Assertions.assertNull(childItem.get(1).getItem());
    }
    
    @Test
    @DisplayName("Map 中包含泛型: HashMap<CustomClass<CustomClass[], Long>, String>")
    public void mapGenericType() {
        JavaActualType genericType = getActualType("complexGenericProperty2");
        
        Assertions.assertEquals(genericType.getName(), "HashMap");
        Assertions.assertEquals(genericType.getClassPath(), "java.util.HashMap");
        Assertions.assertEquals(genericType.getItem().size(), 2);
        
        // 第一个节点是 Description
        JavaActualType firstItem = genericType.getItem().get(0);
        Assertions.assertEquals(firstItem.getName(), "Description");
        Assertions.assertEquals(firstItem.getClassPath(), "com.godone.testSuite.Description");
        Assertions.assertEquals(firstItem.getItem().size(), 2);
        
        // Description 第一个子节点为集合转换而来的 list
        JavaActualType collectItem = firstItem.getItem().get(0);
        Assertions.assertEquals(collectItem.getName(), "List");
        Assertions.assertEquals(collectItem.getClassPath(), "java.util.List");
        Assertions.assertEquals(collectItem.getItem().size(), 1);
        
        // List 子节点为 AuthOperationEnum
        JavaActualType enumItem = collectItem.getItem().get(0);
        Assertions.assertEquals(enumItem.getName(), "AuthOperationEnum");
        Assertions.assertEquals(enumItem.getClassPath(), "com.godone.testSuite.AuthOperationEnum");
        Assertions.assertNull(enumItem.getItem());
    
        // Description 第二个子节点为 Long
        JavaActualType longItem = firstItem.getItem().get(1);
        Assertions.assertEquals(longItem.getName(), "Long");
        Assertions.assertEquals(longItem.getClassPath(), "java.lang.Long");
        Assertions.assertNull(longItem.getItem());
        
        // 第二个节点是 String
        JavaActualType secondItem = genericType.getItem().get(1);
        Assertions.assertEquals(secondItem.getName(), "String");
        Assertions.assertEquals(secondItem.getClassPath(), "java.lang.String");
        Assertions.assertNull(secondItem.getItem());
    }
}
