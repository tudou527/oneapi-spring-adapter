/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.etosun.godone.analysis.Project;
import org.apache.commons.cli.*;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class App {
    // 记录执行时间
    static final Stopwatch stopwatch = Stopwatch.createStarted();
    /**
     * 参数列表
     */
    public static void run(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("o", "output", true, "输出路径");
        options.addOption("p", "project", true, "本地项目根目录");

        CommandLine cmd = parser.parse(options, args);

        if (!cmd.hasOption("p") || !cmd.hasOption("o")) {
            System.out.println("project 参数不存在");
            System.exit(-1);
        }

        new Project(cmd).getSchema();
        // String logText = JSON.toJSONString(projectAnalysis, SerializerFeature.DisableCircularReferenceDetect);
        System.out.printf("execTime: %ss%n", stopwatch.elapsed(TimeUnit.SECONDS));
    }

    /**
     * args.project {string} java 文件地址
     */
    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
