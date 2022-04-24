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
import com.etosun.godone.cache.*;
import com.etosun.godone.models.JavaFileModel;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class Application {
    private String projectPath;
    private String outputFilePath;
    private String localRepository;
    private Integer loopCount = 0;

    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
    @Inject
    private BaseCache baseCache;
    @Inject
    private EntryCache entryCache;
    @Inject
    private ResourceCache resourceCache;
    @Inject
    private FileModelCache modelCache;
    @Inject
    private PendingCache pendingCache;
    
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
    
            log.info("save resource");
            // 缓存入口文件及其他资源文件
            mvnUtil.saveResource(projectPath, true);

            log.info("save reflect class cache");
            // 缓存 JAR 包中的 class
            mvnUtil.saveReflectClassCache(localRepository);

            log.info("analysis entry");
            // 分析入口文件
            entryCache.getCache().forEach(classPath -> {
                String filePath = entryCache.getCache(classPath);
                log.info("analysis file: {}", filePath);
                JavaFileModel fileModel = entryAnalysis.get().analysis(filePath);
                if (fileModel != null) {
                    modelCache.setCache(fileModel);
                }
            });
            
            // 待解析的资源
            log.info("analysis class reference");
            analysisClassReference();
    
            baseCache.clearCache(false);
    
            HashMap<String, JavaFileModel> analysisResult = new HashMap<>();
            modelCache.getCache().forEach((classPath) -> {
                analysisResult.put(classPath, modelCache.getCache(classPath));
            });
            String analysisResultStr = JSON.toJSONString(analysisResult, SerializerFeature.DisableCircularReferenceDetect);
            fileUtil.writeFile(analysisResultStr, outputFilePath+ "/result.json", Charset.defaultCharset());

            // 清空所有缓存
            baseCache.clearCache(true);
    
            log.info("exec time: {} second", stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void analysisClassReference() {
        // 所有待解析的资源
        pendingCache.getCache().forEach((classPath) -> {
            JavaFileModel fileModel;
            String resourceFilePath = resourceCache.getCache(classPath);

            if (resourceFilePath != null) {
                // 先从资源文件中查找
                fileModel = basicAnalysis.get().analysis(resourceFilePath);
            } else {
                // 找不到再从 jar 包中找
                fileModel = reflectAnalysis.get().analysis(classPath);
            }

            if (fileModel != null && fileModel.getClassModel() != null) {
                modelCache.setCache(fileModel);
            }
            pendingCache.removeCache(classPath);
        });
        
        if (pendingCache.getCache().size() > 0 && loopCount < 10) {
            loopCount++;
            analysisClassReference();
        } else {
            log.info("after loop {}, remain {} class", loopCount, pendingCache.getCache().size());
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application app = injector.getInstance(Application.class);

        app.run(args);
    }
}
