/**
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.oneapi.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.EntryAnalysis;
import com.oneapi.spring.cache.*;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.utils.MavenUtil;
import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.apache.commons.cli.*;

import javax.inject.Singleton;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class Application {
    private String projectDir;
    private String outputFileDir;
    private String localRepository;
    private Integer loopCount = 0;
    
    @Inject
    private Logger log;
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
    @Inject
    private EntryCache entryCache;
    @Inject
    private ReflectCache reflectCache;
    @Inject
    private ResourceCache resourceCache;
    @Inject
    private FileModelCache fileModelCache;
    @Inject
    private PendingCache pendingCache;
    @Inject
    Provider<EntryAnalysis> entryAnalysis;
    @Inject
    Provider<BasicAnalysis> basicAnalysis;

    // 记录执行时间
    static final Stopwatch stopwatch = Stopwatch.createStarted();

    private void run(String[] args) {
        boolean testEnv = "test".equals(System.getProperties().getProperty("env"));
        log.info("version: %s", this.getClass().getPackage().getImplementationVersion());
        
        try {
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("o", "output", true, "输出路径");
            options.addOption("p", "project", true, "本地项目根目录");
            options.addOption("r", "repository", true, "mvn 本地仓库");

            CommandLine cmd = parser.parse(options, args);

            if (!cmd.hasOption("p") || !cmd.hasOption("o")) {
                log.info("arguments of p/o is required.");
                if (testEnv) {
                    return;
                } else {
                    System.exit(-200);
                }
            }
    
            projectDir = cmd.getOptionValue("p");
            outputFileDir = cmd.getOptionValue("o");
            localRepository = cmd.getOptionValue("r");
    
            if (localRepository == null) {
                localRepository = System.getProperty("user.home") + "/.m2";
            }
            
            // 复制反编译 jar 包到当前运行目录
            fileUtil.copyDecompiler();
    
            log.info("cache reflect class");
            // 反编译本地 mvn 缓存目录中的 .jar
            mvnUtil.saveReflectClassCache(localRepository);
    
            log.info("cache resource");
            // 缓存入口文件及其他资源文件
            mvnUtil.saveResource(projectDir, true);

            log.info("====== analysis entry ======");
            // 分析入口文件
            entryCache.getCache().forEach(classPath -> {
                JavaFileModel fileModel = entryAnalysis.get().analysis(classPath);
                if (fileModel != null) {
                    fileModelCache.setCache(fileModel);
                }
            });
            
            // 待解析的资源
            log.info("====== analysis resource ======");
            analysisClassReference();
    
            entryCache.clear();
            resourceCache.clear();
            pendingCache.clear();
            reflectCache.clear();
        
            // 解析结果排序
            LinkedHashMap<String, JavaFileModel> analysisResult = new LinkedHashMap<>();
            fileModelCache.getCache().stream().sorted().forEach((classPath) -> {
                analysisResult.put(classPath, fileModelCache.getCache(classPath));
            });
            String analysisResultStr = JSON.toJSONString(analysisResult, SerializerFeature.DisableCircularReferenceDetect);
            fileUtil.writeFile(analysisResultStr, outputFileDir+ "/oneapi.json", Charset.defaultCharset());

            // 清空所有缓存
            fileModelCache.clear();
    
            log.info("exec time: %s second", stopwatch.elapsed(TimeUnit.SECONDS));
            if (!testEnv) {
                System.exit(-200);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void analysisClassReference() {
        // 匹配出待解析的 class 列表
        List<String> paddingClassPath = pendingCache.getCache().stream().filter(c -> "wait".equals(pendingCache.getCache(c))).collect(Collectors.toList());

        // 所有待解析的资源
        paddingClassPath.forEach((classPath) -> {
            JavaFileModel fileModel = basicAnalysis.get().analysis(classPath);
            if (fileModel != null) {
                fileModelCache.setCache(fileModel);
            }
            // 分析一次后无论是否有结果都从队列中删除
            pendingCache.updateCache(classPath);
        });
        
        // 再统计一次
        List<String> remainList = pendingCache.getCache().stream().filter(c -> "wait".equals(pendingCache.getCache(c))).collect(Collectors.toList());
        
        if (remainList.size() > 0 && loopCount < 9) {
            loopCount++;
            analysisClassReference();
        } else {
            if (remainList.size() > 0) {
                log.info("after loop %s, remain %s class", loopCount, pendingCache.getCache().size());
                log.info(String.join("\r\n", pendingCache.getCache()));
            }
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application app = injector.getInstance(Application.class);

        String env = System.getProperties().getProperty("env");
        if (env == null) {
            // 设置一个环境变量，用于区分运行时及单元测试
            System.getProperties().setProperty("env", "prod");
        }
        app.run(args);
    }
}
