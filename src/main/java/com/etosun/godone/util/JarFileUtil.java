/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-18 下午2:34
 */
package com.etosun.godone.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileUtil {
    /**
     * 通过反射得到 jar 包中的 class 以及 path
     *  TODO: 丢了一些数据
     */
    public static void getClasses() {
        String path = "/Users/xiaoyun/.m2/com/alibaba/platform/shared/acl.api/1.3.0-bd-SNAPSHOT-no-parent/";
        String fileName = "acl.api-1.3.0-bd-SNAPSHOT-no-parent.jar";

        try {
            JarFile jarFile = new JarFile(path + fileName);
            Enumeration<JarEntry> e = jarFile.entries();

            JarEntry entry;
            while (e.hasMoreElements()) {
                entry = (JarEntry) e.nextElement();

                if (!entry.getName().contains("META-INF") && entry.getName().contains(".class")) {
                    String classFullName = entry.getName();
                    //去掉后缀.class
                    String className = classFullName.replace("/", ".").replace(".class", "");

                    URL url1 = new URL("file://"+ path + fileName);
                    URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url1}, Thread.currentThread().getContextClassLoader());

                    Class myClass = myClassLoader.loadClass(className);
                    try {
                        Field[] fields = myClass.getFields();
                        for (Field field : fields) {
                            System.out.println(className + " : " + field.getName() + " "+ field.getType());
                        }
                    } catch (Exception ignored) {
                        System.out.println(className);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
