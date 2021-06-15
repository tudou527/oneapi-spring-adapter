/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-18 下午10:33
 */
package com.etosun.godone.util;

import com.thoughtworks.qdox.JavaProjectBuilder;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 获取探测到的文件对象
     */
    private static CodepageDetectorProxy getDetector() {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        // detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());

        return detector;
    }

    /**
     * 根据"encodeType"获取文本编码或文件流编码
     */
    public static Charset getFileOrIOEncode(String path) {
        CodepageDetectorProxy detector = getDetector();
        File file = new File(path);
        Charset charset = null;
        try {
            charset = detector.detectCodepage(file.toURI().toURL());
        } catch (IOException e) {
            //这里获取编码失败,使用系统默认的编码
            charset = Charset.defaultCharset();
        }
        return charset;
    }

    /**
     * 解析文件为 JavaProjectBuilder
     */
    public static JavaProjectBuilder getBuilder(String filePath) {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();
            // 设置文件编码
            builder.setEncoding(getFileOrIOEncode(filePath).name());
            builder.addSourceTree(new File(filePath));

            return builder;
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 从指定目录中匹配文件
     */
    public static List<String> findFile(String glob, String location) {
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
        List<String> targetFile = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if (pathMatcher.matches(path)) {
                        targetFile.add(path.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ignored) {}

        return targetFile;
    }

    /**
     * 写文件
     */
    public static void createFile(String content, String targetPath) {
        try {
            // 生成json格式文件
            File file = new File(targetPath);

            if (!file.getParentFile().exists()) {
                // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(content);
            write.flush();
            write.close();
        } catch (IOException ignore) {
        }
    }
}
