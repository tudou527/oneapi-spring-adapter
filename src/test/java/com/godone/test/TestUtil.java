package com.godone.test;

import com.godone.meta.utils.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.nio.file.Paths;

public class TestUtil {
    static String currentDir = Paths.get("").toAbsolutePath().toString();
    
    public static String getBaseDir() {
        return currentDir +"/src/test/java/";
    }
    
    // 返回测试文件根目录
    public static String getFileByClassPath(String classPath) {
        return getBaseDir() + classPath.replaceAll("\\.", "/") +".java";
    }
    
    // 传入 classPath 返回对应的 class
    public static JavaClass getJavaClass(String classPath) {
        String filePath = getFileByClassPath(classPath);
        FileUtil fileUtil = new FileUtil();
        JavaProjectBuilder javaBuilder = fileUtil.getBuilder(filePath);
        
        return javaBuilder.getClassByName(classPath);
    }
}
