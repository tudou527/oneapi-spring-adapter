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
public class DescriptionTest {
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
    public void normal() {
        JavaCls javaClass = getJavaClass("models/NormalClass.java");

        // 基础信息
        Assert.assertNotNull(javaClass);
        Assert.assertEquals(javaClass.getName(), "NormalClass");
        Assert.assertTrue(javaClass.getClassPath().contains("com.etosun.test"));

        // 描述信息
        Description desc = javaClass.getDescription();
        Assert.assertNotNull(desc);
        Assert.assertEquals(desc.getComment(), "This is class Description");
        HashMap<String, String> descTag = desc.getTag();
        Assert.assertEquals(descTag.get("author"), "tudou527");
    }

}
