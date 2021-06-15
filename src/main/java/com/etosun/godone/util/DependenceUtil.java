/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-20 下午5:23
 */
package com.etosun.godone.util;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DependenceUtil {
    // classPath: gav 的 map 结构
    HashMap<String, String> classMap = new HashMap<>();

    /**
     * 返回 jar 包 gav 坐标
     * @param inputStream xml 文件流
     * @return JAR 包 gav 坐标信息
     */
    private Dependency parserGAV(InputStream inputStream) {
        Model pomModel = null;
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            pomModel = reader.read(inputStream);
        } catch (IOException | XmlPullParserException ignored) {
        }

        if (pomModel != null) {
            String groupId = pomModel.getGroupId();
            String artifactId = pomModel.getArtifactId();
            String version = pomModel.getVersion();

            if (groupId == null && pomModel.getParent() != null) {
                groupId = pomModel.getParent().getGroupId();
            }

            if (groupId != null && artifactId != null && version != null) {
                String finalGroupId = groupId;
                return new Dependency(){{
                    setGroupId(finalGroupId);
                    setArtifactId(artifactId);
                    setVersion(version);
                }};
            }
        }

        return null;
    }

    /**
     * 解析 jar 包中的类
     * @return HashMap<String, String> 返回 classPath: gav 的 map 结构
     */
    private HashMap<String, String> getInfo(String jarPath) {
        Dependency dependency = null;
        // jar 包中的 classPath
        ArrayList<String> classPath = new ArrayList<>();

        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> e = jarFile.entries();

            JarEntry entry;
            while (e.hasMoreElements()) {
                entry = e.nextElement();

                if (!entry.getName().contains("META-INF") && entry.getName().contains(".class")) {
                    String classFullName = entry.getName();
                    // 去掉后缀 .class
                    classPath.add(classFullName.substring(0, classFullName.length() - 6).replace("/", "."));
                }

                if (entry.getName().endsWith("/pom.xml")) {
                    dependency = parserGAV(jarFile.getInputStream(entry));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dependency != null && classPath.size() > 0) {
            String depStr = String.format("%s/%s/%s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());

            HashMap<String, String> classInfo = new HashMap<>();
            classPath.forEach(path -> {
                classInfo.putIfAbsent(path, depStr);
            });

            return classInfo;
        }

        return null;
    }

    /**
     * 解析指定目录下所有 jar 包中的 classPath 与 gav 坐标映射关系
     */
    public void analysis(String repository) {
        // 找到所有 .jar 文件
        List<String> jarFile = FileUtil.findFile("glob:**/*.jar", repository);

        for (String jar : jarFile) {
            HashMap<String, String> classInfo = getInfo(jar);
            if (classInfo != null) {
                classMap.putAll(classInfo);
            }
        }
    }
}
