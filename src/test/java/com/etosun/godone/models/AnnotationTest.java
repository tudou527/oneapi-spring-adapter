/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:29
 */
package com.etosun.godone.models;

import com.etosun.godone.analysis.ClassAnalysis;
import com.etosun.godone.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotationTest {
    private JavaCls getJavaClass(String fileName) {
        String filePath = getClass().getResource("/").getPath() + fileName;

        JavaFile javaFile = new JavaFile();
        javaFile.setFilePath(filePath);

        JavaProjectBuilder builder = FileUtil.getBuilder(javaFile.getFilePath());
        Assert.assertNotNull(builder);

        JavaClass javaClass = (JavaClass) builder.getClasses().toArray()[0];
        Assert.assertNotNull(javaClass);

        return new ClassAnalysis(javaClass).run();
    }

    @Test()
    public void normalTest() {
        JavaCls javaClass = getJavaClass("models/NormalClass.java");

        // class 注解
        ArrayList<Annotation> annotation = javaClass.getAnnotation();
        Assert.assertEquals(annotation.get(0).getName(), "RunWith");
        Assert.assertEquals(annotation.get(0).getClassPath(), "org.junit.runner.RunWith");
        HashMap<String, Object> hashMapRunAn = annotation.get(0).getFields();
        Assert.assertEquals(hashMapRunAn.get("value"), "org.springframework.test.context.junit4.SpringRunner.class");

        Assert.assertEquals(annotation.get(1).getName(), "SpringBootTest");
        Assert.assertEquals(annotation.get(1).getClassPath(), "org.springframework.boot.test.context.SpringBootTest");
        Assert.assertEquals(annotation.get(1).getFields().size(), 0);
    }

}
