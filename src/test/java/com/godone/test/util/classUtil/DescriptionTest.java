package com.godone.test.util.classUtil;

import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@DisplayName("classUtil.getDescription")
public class DescriptionTest {
    private List<String> fileLines;
    @InjectMocks
    ClassUtil classUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    
        try {
            FileUtil fileUtil = new FileUtil();
            String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.Description");
            fileLines = Files.readAllLines(Paths.get(filePath), fileUtil.getFileOrIOEncode(filePath));
        } catch (IOException ignored) {
        }
    }

    @Test
    @DisplayName("多行注释")
    public void methodMultiDescription() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.Description");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(f -> f.getName().equals("methodA")).findFirst();

        Assertions.assertTrue(method.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(method.get(), fileLines);

        Assertions.assertEquals(desc.getText(), "methodA\n多行注释");
        Assertions.assertEquals(desc.getTag().get("deprecated").get(0), "不久之后废弃");
    }

    @Test
    @DisplayName("单行注释")
    public void methodSingleDescription() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.Description");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(f -> f.getName().equals("methodB")).findFirst();

        Assertions.assertTrue(method.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(method.get(), fileLines);

        Assertions.assertEquals(desc.getText(), "methodB 单行注释");
    }
    
    @Test
    @DisplayName("参数类注释")
    public void paramsDescription() {
        JavaClass javaClass = TestUtil.getJavaClass("com.godone.testSuite.Description");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(f -> f.getName().equals("methodC")).findFirst();
    
        Assertions.assertTrue(method.isPresent());
    
        JavaDescriptionModel desc = classUtil.getDescription(method.get(), fileLines);
        Assertions.assertEquals(desc.getText(), "methodC 方法注释");
        
        HashMap<String, List<String>> tags = desc.getTag();
        Assertions.assertEquals(tags.size(), 2);
    
        Assertions.assertEquals(tags.get("param"), new ArrayList<String>(){{
            add("a 参数 A");
            add("b 参数 B");
            add("c 参数");
        }});
        Assertions.assertEquals(tags.get("return"), new ArrayList<String>(){{
            add("void");
        }});
    }
}
