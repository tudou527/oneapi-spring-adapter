/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.analysis.JavaFileAnalysis;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.FileUtil;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.commons.cli.*;
import com.google.common.base.Stopwatch;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
public class Application {
    private String projectPath;
    private String outputFilePath;

    @Inject
    private FileUtil fileUtil;
    @Inject
    private JavaFileAnalysis javaFileAnalysis;

    // 记录执行时间
    static final Stopwatch stopwatch = Stopwatch.createStarted();

    private void run(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("o", "output", true, "输出路径");
            options.addOption("p", "project", true, "本地项目根目录");

            CommandLine cmd = parser.parse(options, args);

            if (!cmd.hasOption("p") || !cmd.hasOption("o")) {
                System.out.println("project 参数不存在");
                System.exit(-1);
            }

            projectPath = cmd.getOptionValue("p");
            outputFilePath = cmd.getOptionValue("o");

            getSchema();

            System.out.printf("execTime: %ss%n", stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getSchema() {
        ArrayList<JavaFileModel> fileModels = new ArrayList<>();

        // 解析项目目录下所有 .java 文件
        fileUtil.findFileList("glob:**/*.java", projectPath).forEach(file -> {
            fileModels.add(javaFileAnalysis.analysis(file));
        });

        String result = JSON.toJSONString(fileModels, SerializerFeature.DisableCircularReferenceDetect);
        fileUtil.writeFile(result, outputFilePath + "/result.json", null);

        System.out.printf("found %s files%n", fileModels.size());
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application app = injector.getInstance(Application.class);
        app.run(args);
    }
}
