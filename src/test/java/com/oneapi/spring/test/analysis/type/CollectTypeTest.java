package com.oneapi.spring.test.analysis.type;

import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.test.TestUtil;
import com.oneapi.spring.utils.Logger;
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
public class CollectTypeTest {
    @Mock
    Logger log;
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
    
        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }
    
    private JavaActualType getActualType(String fieldName) {
        JavaFileModel fileModel = new JavaFileModel();
        fileModel.setImports(new ArrayList<String>(){{
            add("java.util.HashMap");
            add("com.oneapi.spring.testSuite.Description");
            add("com.oneapi.spring.testSuite.AuthOperationEnum");
        }});
        
        JavaClass javaClass = TestUtil.getJavaClass("com.oneapi.spring.testSuite.field.ComplexField");
        fileModel.setJavaSource(javaClass.getSource());
        
        Assertions.assertNotNull(javaClass);
        
        return typeAnalysis.analysis(javaClass.getFieldByName(fieldName).getType(), fileModel);
    }
    
    @Test
    @DisplayName("泛型集合: T[]")
    public void collectGenericType() {
        JavaActualType genericType = getActualType("genericArr");
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(), "java.util.List");
        Assertions.assertEquals(genericType.getItems().size(), 1);
        
        Assertions.assertEquals(genericType.getItems().get(0).getName(), "T");
        Assertions.assertEquals(genericType.getItems().get(0).getClassPath(), "T");
        Assertions.assertNull(genericType.getItems().get(0).getItems());
    }
    
    @Test
    @DisplayName("列表+泛型+集合: List<T[]>")
    public void listCollectGenericType() {
        JavaActualType genericType = getActualType("genericArrList");
        
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(genericType.getItems().size(),  1);
        
        // 子节点仍然是被转换之后的 list
        JavaActualType itemType = genericType.getItems().get(0);
        Assertions.assertEquals(itemType.getName(), "List");
        Assertions.assertEquals(itemType.getClassPath(), "java.util.List");
        Assertions.assertEquals(itemType.getItems().size(),  1);
    
        JavaActualType childType = itemType.getItems().get(0);
        Assertions.assertEquals(childType.getName(), "T");
        Assertions.assertEquals(childType.getClassPath(), "T");
        Assertions.assertNull(childType.getItems());
    }
    
    @Test
    @DisplayName("多泛型集合: CustomClass<T, U>[]")
    public void collectGenericClass() {
        JavaActualType genericType = getActualType("customGenericArr");
        
        // 集合转换为 list
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(), "java.util.List");
        Assertions.assertEquals(genericType.getItems().size(), 1);
        
        // 子节点为原来的类型
        Assertions.assertEquals(genericType.getItems().get(0).getName(), "Description");
        Assertions.assertEquals(genericType.getItems().get(0).getClassPath(), "com.oneapi.spring.testSuite.Description");
        
        ArrayList<JavaActualType> itemActualType = genericType.getItems().get(0).getItems();
        Assertions.assertEquals(itemActualType.size(), 2);
        
        Assertions.assertEquals(itemActualType.get(0).getName(),  "String");
        Assertions.assertEquals(itemActualType.get(0).getClassPath(), "java.lang.String");
        Assertions.assertNull(itemActualType.get(0).getItems());
        
        Assertions.assertEquals(itemActualType.get(1).getName(),  "String");
        Assertions.assertEquals(itemActualType.get(1).getClassPath(), "java.lang.String");
        Assertions.assertNull(itemActualType.get(1).getItems());
    }
    
    @Test
    @DisplayName("集合嵌套: CustomClass<T, U>[][]")
    public void doubleDimensionality() {
        JavaActualType genericType = getActualType("customGenericArrOfArr");
        
        // 前 2 级都应该是 list
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(genericType.getItems().size(),  1);
        
        JavaActualType childType = genericType.getItems().get(0);
        Assertions.assertEquals(childType.getName(),  "List");
        Assertions.assertEquals(childType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(childType.getItems().size(),  1);
        
        // item 类型
        JavaActualType itemType = childType.getItems().get(0);
        Assertions.assertEquals(itemType.getName(),  "Description");
        Assertions.assertEquals(itemType.getClassPath(),  "com.oneapi.spring.testSuite.Description");
        Assertions.assertEquals(itemType.getItems().size(),  2);
    
        Assertions.assertEquals(itemType.getItems().get(0).getName(),  "String");
        Assertions.assertEquals(itemType.getItems().get(0).getClassPath(),  "java.lang.String");
        Assertions.assertNull(itemType.getItems().get(0).getItems());
    
        Assertions.assertEquals(itemType.getItems().get(1).getName(),  "String");
        Assertions.assertEquals(itemType.getItems().get(1).getClassPath(),  "java.lang.String");
        Assertions.assertNull(itemType.getItems().get(1).getItems());
    }
}
