/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:12
 */
package com.etosun.godone.analysis;

import com.etosun.godone.models.JavaFile;
import com.etosun.godone.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ProjectAnalysis {
    public ArrayList<JavaFile> run(HashMap<String, String> arguments) {
        ArrayList<JavaFile> fileList = new ArrayList<>();
        // 解析 repository 中的所有依赖
        // new DependenceUtil().analysis(arguments.get("repository"));

        // 找到所有的 .java 文件
        FileUtil.findFile("glob:**/*.java", arguments.get("project")).forEach(file -> {
            JavaProjectBuilder builder = FileUtil.getBuilder(file);

            if (builder == null || file.contains("/src/test/")) {
                return;
            }

            JavaFile javaFile = new JavaFile();
            javaFile.setFilePath(file);

            Optional<JavaClass> firstClass = builder.getClasses().stream().findFirst();
            if (firstClass.isPresent()) {
                javaFile.setPackgeName(firstClass.get().getSource().getPackageName());
                javaFile.setImports(firstClass.get().getSource().getImports());
            }

            // 解析 class
            builder.getClasses().forEach(cls -> {
                javaFile.getClassList().add(new ClassAnalysis(cls).run());
            });

            fileList.add(javaFile);
        });

        return fileList;
    }
}
