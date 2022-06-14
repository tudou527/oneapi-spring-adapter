package com.godone.test.analysis.type;

import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
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
public class CollectTypeTest {
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
    @DisplayName("泛型集合: T[]")
    public void collectGenericType() {
        JavaActualType genericType = getActualType("genericArr");
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(), "java.util.List");
        Assertions.assertEquals(genericType.getItem().size(), 1);
        
        Assertions.assertEquals(genericType.getItem().get(0).getName(), "T");
        Assertions.assertEquals(genericType.getItem().get(0).getClassPath(), "T");
        Assertions.assertNull(genericType.getItem().get(0).getItem());
    }
    
    @Test
    @DisplayName("列表+泛型+集合: List<T[]>")
    public void listCollectGenericType() {
        JavaActualType genericType = getActualType("genericArrList");
        
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(genericType.getItem().size(),  1);
        
        // 子节点仍然是被转换之后的 list
        JavaActualType itemType = genericType.getItem().get(0);
        Assertions.assertEquals(itemType.getName(), "List");
        Assertions.assertEquals(itemType.getClassPath(), "java.util.List");
        Assertions.assertEquals(itemType.getItem().size(),  1);
    
        JavaActualType childType = itemType.getItem().get(0);
        Assertions.assertEquals(childType.getName(), "T");
        Assertions.assertEquals(childType.getClassPath(), "T");
        Assertions.assertNull(childType.getItem());
    }
    
    @Test
    @DisplayName("多泛型集合: CustomClass<T, U>[]")
    public void collectGenericClass() {
        JavaActualType genericType = getActualType("customGenericArr");
        
        // 集合转换为 list
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(), "java.util.List");
        Assertions.assertEquals(genericType.getItem().size(), 1);
        
        // 子节点为原来的类型
        Assertions.assertEquals(genericType.getItem().get(0).getName(), "Description");
        Assertions.assertEquals(genericType.getItem().get(0).getClassPath(), "com.godone.testSuite.Description");
        
        ArrayList<JavaActualType> itemActualType = genericType.getItem().get(0).getItem();
        Assertions.assertEquals(itemActualType.size(), 2);
        
        Assertions.assertEquals(itemActualType.get(0).getName(),  "String");
        Assertions.assertEquals(itemActualType.get(0).getClassPath(), "java.lang.String");
        Assertions.assertNull(itemActualType.get(0).getItem());
        
        Assertions.assertEquals(itemActualType.get(1).getName(),  "String");
        Assertions.assertEquals(itemActualType.get(1).getClassPath(), "java.lang.String");
        Assertions.assertNull(itemActualType.get(1).getItem());
    }
    
    @Test
    @DisplayName("集合嵌套: CustomClass<T, U>[][]")
    public void doubleDimensionality() {
        JavaActualType genericType = getActualType("customGenericArrOfArr");
        
        // 前 2 级都应该是 list
        Assertions.assertEquals(genericType.getName(),  "List");
        Assertions.assertEquals(genericType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(genericType.getItem().size(),  1);
        
        JavaActualType childType = genericType.getItem().get(0);
        Assertions.assertEquals(childType.getName(),  "List");
        Assertions.assertEquals(childType.getClassPath(),  "java.util.List");
        Assertions.assertEquals(childType.getItem().size(),  1);
        
        // item 类型
        JavaActualType itemType = childType.getItem().get(0);
        Assertions.assertEquals(itemType.getName(),  "Description");
        Assertions.assertEquals(itemType.getClassPath(),  "com.godone.testSuite.Description");
        Assertions.assertEquals(itemType.getItem().size(),  2);
    
        Assertions.assertEquals(itemType.getItem().get(0).getName(),  "String");
        Assertions.assertEquals(itemType.getItem().get(0).getClassPath(),  "java.lang.String");
        Assertions.assertNull(itemType.getItem().get(0).getItem());
    
        Assertions.assertEquals(itemType.getItem().get(1).getName(),  "String");
        Assertions.assertEquals(itemType.getItem().get(1).getClassPath(),  "java.lang.String");
        Assertions.assertNull(itemType.getItem().get(1).getItem());
    }
}
