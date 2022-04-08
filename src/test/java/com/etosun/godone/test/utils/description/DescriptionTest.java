package com.etosun.godone.test.utils.description;

import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.utils.ClassUtil;

import com.etosun.godone.utils.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@DisplayName("注释")
public class DescriptionTest {
    private final ClassUtil classUtil = new ClassUtil();
    private List<String> fileLines;
    private JavaProjectBuilder javaBuilder;

    @BeforeEach
    public void getBuilder() {
        String filePath = getClass().getClassLoader().getResource("java/resource/Description.java").getFile();
        FileUtil fileUtil = new FileUtil();

        try {
            fileLines = Files.readAllLines(Paths.get(filePath), fileUtil.getFileOrIOEncode(filePath));
        } catch (IOException ignored) {
        }

        javaBuilder = fileUtil.getBuilder(filePath);
    }

    @Test
    @DisplayName("class 多行注释")
    public void classMultiDescription() {
        JavaClass javaClass = javaBuilder.getClassByName("com.etosun.godone.test.Description");

        Assertions.assertNotNull(javaClass);

        JavaDescriptionModel desc = classUtil.getDescription(javaClass, fileLines);

        Assertions.assertEquals(desc.getText(), "class\n多行注释");
        Assertions.assertEquals(desc.getTag().get("date"), "2022-04-08");
        Assertions.assertEquals(desc.getTag().get("author"), "authorName");
    }

    @Test
    @DisplayName("method 多行注释")
    public void methodMultiDescription() {
        JavaClass javaClass = javaBuilder.getClassByName("com.etosun.godone.test.Description");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(f -> f.getName().equals("methodA")).findFirst();

        Assertions.assertTrue(method.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(method.get(), fileLines);

        Assertions.assertEquals(desc.getText(), "methodA\n多行注释");
        Assertions.assertEquals(desc.getTag().get("deprecated"), "不久之后废弃");
    }

    @Test
    @DisplayName("method 单行注释")
    public void methodSingleDescription() {
        JavaClass javaClass = javaBuilder.getClassByName("com.etosun.godone.test.Description");
        Optional<JavaMethod> method = javaClass.getMethods().stream().filter(f -> f.getName().equals("methodB")).findFirst();

        Assertions.assertTrue(method.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(method.get(), fileLines);

        Assertions.assertEquals(desc.getText(), "methodB 单行注释");
    }

    @Test
    @DisplayName("field 多行注释")
    public void fieldMultiDescription() {
        JavaClass javaClass = javaBuilder.getClassByName("com.etosun.godone.test.Description");
        Optional<JavaField> field = javaClass.getFields().stream().filter(f -> f.getName().equals("fieldName2")).findFirst();

        Assertions.assertTrue(field.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(field.get(), fileLines);
        Assertions.assertEquals(desc.getText(), "fieldName2\n多行注释");
        Assertions.assertEquals(desc.getTag().get("author"), "author1");
    }

    @Test
    @DisplayName("field 单行注释")
    public void fieldSingleDescription() {
        JavaClass javaClass = javaBuilder.getClassByName("com.etosun.godone.test.Description");
        Optional<JavaField> field = javaClass.getFields().stream().filter(f -> f.getName().equals("fieldName1")).findFirst();

        Assertions.assertTrue(field.isPresent());

        JavaDescriptionModel desc = classUtil.getDescription(field.get(), fileLines);
        Assertions.assertEquals(desc.getText(), "fieldName1 单行注释");
    }
}
