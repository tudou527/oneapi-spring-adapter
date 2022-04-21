/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.EntryAnalysis;
import com.etosun.godone.analysis.ReflectAnalysis;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.CommonCache;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class Application {
    private String projectPath;
    private String outputFilePath;
    private String localRepository;
    private Integer loopCount = 0;

    @Inject
    private CommonCache commonCache;
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
    @Inject
    Provider<EntryAnalysis> entryAnalysis;
    @Inject
    Provider<BasicAnalysis> basicAnalysis;
    @Inject
    Provider<ReflectAnalysis> reflectAnalysis;

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
            
            commonCache.setProjectPath(projectPath);
            commonCache.setLocalRepository(localRepository);

            // 缓存入口文件及其他资源文件
            mvnUtil.saveResource(projectPath, true);
            // 缓存 JAR 包中的 class
            mvnUtil.saveReflectClassCache(localRepository);
    
            // 分析入口文件
            commonCache.getEntry().forEach(entry -> {
                log.info("analysis file: {}", entry);

                JavaFileModel fileModel = entryAnalysis.get().analysis(entry);
                if (fileModel != null) {
                    commonCache.saveModel(fileModel.getClassModel().getClassPath(), fileModel);
                }
            });
            
            // 待解析的资源
            analysisResource();
    
            log.info("execTime: {}", stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void analysisResource() {
        // 所有待解析的资源
        commonCache.getPaddingClassPath().forEach(classPath -> {
            JavaFileModel fileModel;
            String resourceFilePath = commonCache.getResource(classPath);
            
            if (resourceFilePath != null) {
                fileModel = basicAnalysis.get().analysis(resourceFilePath);
            } else {
                fileModel = reflectAnalysis.get().analysis(classPath);
            }

            if (fileModel != null && fileModel.getClassModel() != null) {
                commonCache.saveModel(fileModel.getClassModel().getClassPath(), fileModel);
            }
            commonCache.removePaddingClassPath(classPath);
        });
        
        if (commonCache.getPaddingClassPath().size() > 0 && loopCount < 10) {
            loopCount++;
            log.info("loop: {}", loopCount);
            analysisResource();
        } else {
            log.info("timeout: {}", commonCache.getPaddingClassPath().size());
        }
    
        String result = JSON.toJSONString(commonCache.getModel(), SerializerFeature.DisableCircularReferenceDetect);
        fileUtil.writeFile(result, outputFilePath + "/result.json", null);
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application app = injector.getInstance(Application.class);
        app.run(args);
    }
}
