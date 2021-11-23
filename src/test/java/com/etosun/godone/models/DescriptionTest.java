/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:29
 */
package com.etosun.godone.models;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DescriptionTest {
    private JavaClassModel getJavaClass(String fileName) {
        String filePath = getClass().getResource("/").getPath() + fileName;

        JavaFileModel javaFile = new JavaFileModel();

        return null;
    }

    @Test()
    public void normal() {
        JavaClassModel javaClass = getJavaClass("models/NormalClass.java");

        // 基础信息
        Assert.assertNotNull(javaClass);
        Assert.assertEquals(javaClass.getName(), "NormalClass");
        Assert.assertTrue(javaClass.getClassPath().contains("com.etosun.test"));

        // 描述信息
        JavaDescriptionModel desc = javaClass.getDescription();
        Assert.assertNotNull(desc);
        Assert.assertEquals(desc.getText(), "This is class Description");
        HashMap<String, String> descTag = desc.getTag();
        Assert.assertEquals(descTag.get("author"), "tudou527");
    }

}
