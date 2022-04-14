/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.analysis.EntryAnalysis;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.Logger;
import com.etosun.godone.utils.MavenUtil;
import com.etosun.godone.utils.CommonCache;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.apache.commons.cli.*;
import com.google.common.base.Stopwatch;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
public class Application {
    private String projectPath;
    private String outputFilePath;
    private String localRepository;

    @Inject
    private CommonCache commonCache;
    @Inject
    private FileUtil fileUtil;
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private Logger logger;
    @Inject
    Provider<EntryAnalysis> entryAnalysis;

    // 记录执行时间
    static final Stopwatch stopwatch = Stopwatch.createStarted();

    private void run(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("o", "output", true, "输出路径");
            options.addOption("p", "project", true, "本地项目根目录");
            options.addOption("r", "repository", true, "mvn 本地仓库");

            CommandLine cmd = parser.parse(options, args);

            if (!cmd.hasOption("p") || !cmd.hasOption("o") || !cmd.hasOption("r")) {
                System.out.println("参数不完整");
                System.exit(-1);
            }

            projectPath = cmd.getOptionValue("p");
            outputFilePath = cmd.getOptionValue("o");
            localRepository = cmd.getOptionValue("r");

            // 缓存入口文件及其他资源文件
            mvnUtil.saveResource(projectPath, true);
            // 缓存 JAR 包中的 class
            mvnUtil.saveReflectClassCache(localRepository);

            getSchema();
    
            logger.message("execTime: %s", stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getSchema() {
        // 分析入口文件
        commonCache.getEntry().forEach(entry -> {
            logger.message("analysis file: %s", entry);
            JavaFileModel fileModel = entryAnalysis.get().analysis(entry);
            if (fileModel != null) {
                commonCache.saveModel(fileModel.getClassModel().getClassPath(), fileModel);
            }
        });

        String result = JSON.toJSONString(commonCache.getModel(), SerializerFeature.DisableCircularReferenceDetect);
        fileUtil.writeFile(result, outputFilePath + "/result.json", null);
        
        System.out.print(String.join("\n", commonCache.getPaddingClassPath()));

        logger.message("found %s files%n", commonCache.getModel().size());
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application app = injector.getInstance(Application.class);
        app.run(args);
    }
}
