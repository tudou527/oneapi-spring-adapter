package com.etosun.godone.test.utils;

import com.etosun.godone.models.JavaAnnotationModel;
import com.etosun.godone.models.JavaFileModel;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import org.junit.jupiter.api.DisplayName;

import com.etosun.godone.utils.ClassUtil;

import com.etosun.godone.utils.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@DisplayName("注解")
public class AnnotationTest {
    private final ClassUtil classUtil = new ClassUtil();
    JavaFileModel mockFileModel = Mockito.mock(JavaFileModel.class);

    @BeforeEach
    public void mockJavaFileModel() {
        when(mockFileModel.getImports()).thenReturn(new ArrayList<String>(){{
            add("org.springframework.ui.Model");
            add("org.springframework.web.bind.annotation.RestController");
            add("org.springframework.web.bind.annotation.RequestMapping");
            add("org.springframework.web.bind.annotation.PostMapping");
            add("org.springframework.web.bind.annotation.GetMapping");
            add("org.springframework.web.bind.annotation.RequestBody");
            add("org.springframework.web.bind.annotation.ResponseBody");
            add("com.alibaba.brain.job.common.log.BizMonitorDefinition");
        }});
    }

    public JavaClass getJavaClass(String classPath) {
        String filePath = Objects.requireNonNull(getClass().getClassLoader().getResource("java/resource/Annotation.java")).getFile();
        FileUtil fileUtil = new FileUtil();
        JavaProjectBuilder javaBuilder = fileUtil.getBuilder(filePath);
    
        return javaBuilder.getClassByName(classPath);
    }

    @Test
    @DisplayName("class 注解")
    public void classAnnotation() {
        JavaClass javaClass = getJavaClass("com.etosun.godone.test.Annotation");

        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(javaClass.getAnnotations(), mockFileModel);

        // 应该解析到 2 个注解
        Assertions.assertEquals(annotations.size() , 2);

        // 第 1 个注解信息
        JavaAnnotationModel restAnnotation = annotations.get(0);
        Assertions.assertEquals(restAnnotation.getName(), "RestController");
        Assertions.assertEquals(restAnnotation.getClassPath(), "org.springframework.web.bind.annotation.RestController");
        Assertions.assertEquals(restAnnotation.getFields().size(), 0);

        // 第 2 个注解信息
        JavaAnnotationModel reqAnnotation = annotations.get(1);
        Assertions.assertEquals(reqAnnotation.getName(), "RequestMapping");
        Assertions.assertEquals(reqAnnotation.getClassPath(), "org.springframework.web.bind.annotation.RequestMapping");
        Assertions.assertEquals(reqAnnotation.getFields().size(), 1);

        HashMap<String, Object> anField = reqAnnotation.getFields();
        Assertions.assertEquals(anField.get("value"), "/staffJob");
    }

    @Test
    @DisplayName("method 注解")
    public void methodAnnotation() {
        JavaClass javaClass = getJavaClass("com.etosun.godone.test.Annotation");
        Optional<JavaMethod> optionalMethod = javaClass.getMethods().stream().filter(m -> m.getName().contains("index")).findFirst();

        Assertions.assertTrue(optionalMethod.isPresent());
        JavaMethod method = optionalMethod.get();

        ArrayList<JavaAnnotationModel> annotations = classUtil.getAnnotation(method.getAnnotations(), mockFileModel);

        // 应该解析到 2 个注解
        Assertions.assertEquals(annotations.size() , 3);

        // 第 1 个注解信息
        JavaAnnotationModel postAnnotation = annotations.get(0);
        Assertions.assertEquals(postAnnotation.getName(), "PostMapping");
        Assertions.assertEquals(postAnnotation.getClassPath(), "org.springframework.web.bind.annotation.PostMapping");
        Assertions.assertEquals(postAnnotation.getFields().size(), 1);
        Assertions.assertEquals(postAnnotation.getFields().get("value"), "upload");

        // 第 2 个注解信息
        JavaAnnotationModel resAnnotation = annotations.get(1);
        Assertions.assertEquals(resAnnotation.getName(), "ResponseBody");
        Assertions.assertEquals(resAnnotation.getClassPath(), "org.springframework.web.bind.annotation.ResponseBody");
        Assertions.assertEquals(resAnnotation.getFields().size(), 0);

        // 第 3 个注解
        JavaAnnotationModel bizAnnotation = annotations.get(2);
        Assertions.assertEquals(bizAnnotation.getName(), "BizMonitorDefinition");
        Assertions.assertEquals(bizAnnotation.getClassPath(), "com.alibaba.brain.job.common.log.BizMonitorDefinition");
        Assertions.assertEquals(bizAnnotation.getFields().size(), 2);
        Assertions.assertEquals(bizAnnotation.getFields().get("operationCode"), "upload");
        Assertions.assertEquals(bizAnnotation.getFields().get("operationName"), "人员导入");
    }

    @Test
    @DisplayName("field 注解")
    public void fieldAnnotation() {
        JavaClass javaClass = getJavaClass("com.etosun.godone.test.Annotation");

        List<ArrayList<JavaAnnotationModel>> fieldAns = javaClass.getFields().stream()
                .map(field -> classUtil.getAnnotation(field.getAnnotations(), mockFileModel)).collect(Collectors.toList());

        Assertions.assertEquals(fieldAns.size(),2);
        Assertions.assertNull(fieldAns.get(1));

        ArrayList<JavaAnnotationModel> requestAn = fieldAns.get(0);
        Assertions.assertEquals(requestAn.size(), 1);

        JavaAnnotationModel wiredAn = requestAn.get(0);
        Assertions.assertEquals(wiredAn.getName(), "Autowired");
        Assertions.assertNull(wiredAn.getClassPath());

        HashMap<String, Object> fields = wiredAn.getFields();
        Assertions.assertEquals(fields.size(), 4);

        List<String> nameValues = new ArrayList<String>(){{
            add("a");
            add("b");
            add("c");
        }};
        Assertions.assertEquals(fields.get("name"), nameValues);
        Assertions.assertEquals(fields.get("index"), 2);
        Assertions.assertEquals(fields.get("required"), false);
        Assertions.assertEquals(fields.get("value"), "anValue");
    }
}