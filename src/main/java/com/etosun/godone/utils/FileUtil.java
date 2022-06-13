/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-18 下午10:33
 */
package com.etosun.godone.utils;

import com.thoughtworks.qdox.JavaProjectBuilder;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

@Slf4j
@Singleton
public class FileUtil {
    /**
     * 获取探测到的文件对象
     */
    private CodepageDetectorProxy getDetector() {
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
    public Charset getFileOrIOEncode(String path) {
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
    public JavaProjectBuilder getBuilder(String filePath) {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();
            // 设置文件编码
            builder.setEncoding(getFileOrIOEncode(filePath).name());
            builder.addSource(new File(filePath));

            return builder;
        } catch (Exception ignore) {
        }

        return null;
    }

    // 从指定目录中匹配文件
    public List<String> findFileList(String glob, String location) {
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

    // 写文件
    public void writeFile(String content, String targetPath, Charset encode) {
        try {
            // 生成json格式文件
            File file = new File(targetPath);
    
            if (file.exists()) {
                // 如果已存在,删除旧文件
                file.delete();
            }

            if (!file.getParentFile().exists()) {
                // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }

            file.createNewFile();

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), encode != null ? encode : StandardCharsets.UTF_8);
            write.write(content);
            write.flush();
            write.close();
        } catch (IOException ignore) {
        }
    }

    /**
     * 解压 jar 包
     * @param jarFile 目标 jar 包
     * @param target 解压目录
     */
    public void unzipJar(File jarFile, String target) {
        // 时间戳文件名
        String timestamp = String.format("%s/.godone.unzip_success", target);

        // 不重复解压
        if (new File(timestamp).exists()) {
            return;
        }

        try {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> enumEntries = jar.entries();

            File targetFile = new File(target);
            if (!targetFile.exists()) {
                // 如果目录不存在，创建目录
                targetFile.mkdir();
            }

            while (enumEntries.hasMoreElements()) {
                JarEntry file = enumEntries.nextElement();
                File f = new File(target + java.io.File.separator + file.getName());
                if (file.isDirectory()) {
                    f.mkdir();
                    continue;
                } else {
                    if (!f.getParentFile().exists()) {
                        // 如果父目录不存在，创建父目录
                        f.getParentFile().mkdirs();
                    }

                    // 只解压白名单文件文件
                    ArrayList<String> whitelist = new ArrayList<String>(){{
                        add(".java");
                        add(".xml");
                    }};
                    if (whitelist.stream().noneMatch(b -> f.getName().toLowerCase().endsWith(b))) {
                        continue;
                    }
                }
                java.io.InputStream is = jar.getInputStream(file);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                fos.close();
                is.close();
            }
            jar.close();

            writeFile("done", timestamp, Charset.defaultCharset());

            // 退出时删除解压目录
            targetFile.deleteOnExit();
        } catch (IOException ignore) {
        }
    }
    
    /**
     * 执行命令行
     * @param cmd string[] 待执行的命令
     * @param workDir string 执行目录
     */
    public void exec(String[] cmd, String workDir) {
        try {
            log.info(String.format("Run command: %s", String.join(" ", cmd)));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(workDir));
            Process process = pb.start();
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorBuffer = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String inputLine;
            String errLine;
            // 过滤掉下载过程的日志输出
            Pattern pattern = Pattern.compile("\\s(KB|MB|kB|mB)+");
            // 直到读完为止
            // while((inputLine = inputBuffer.readLine()) != null) {
            //   if (!pattern.matcher(inputLine).find() && !inputLine.trim().isEmpty()) {
            //      System.out.println(inputLine);
            //   }
            // }
            while((errLine = errorBuffer.readLine()) !=null){
                System.out.println(errLine);
            }
            process.waitFor();
            log.info("Run command done.");
        } catch (Exception e) {
            log.info(String.format("Run Command: %s in %s error: ", String.join(" ", cmd), workDir));
        }
    }
}
