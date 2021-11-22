/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.analysis.ProjectAnalysis;
import com.etosun.godone.models.JavaFile;
import com.etosun.godone.util.FileUtil;
import com.etosun.godone.util.JarFileUtil;
import com.etosun.godone.util.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SpringBootApplication
public class App {
    public static void run(String[] args) {
        HashMap<String, String> arguments = new HashMap<>();

        Arrays.asList(args).forEach(arg -> {
            String[] str = arg.split("=");
            arguments.put(str[0], str[1]);
        });

        // 打印 request 参数方便排查
        if (arguments.get("project") == null) {
            Logger.info("参数错误，缺少 project 参数");
            return;
        }
        if (arguments.get("savePath") == null) {
            Logger.info("参数错误，缺少 savePath 参数");
            return;
        }
        if (arguments.get("repository") == null) {
            Logger.info("参数错误，缺少 repository 参数");
            return;
        }

        Logger.info(arguments);

        JarFileUtil.getClasses();

        ArrayList<JavaFile> result = new ProjectAnalysis().run(arguments);
        String content = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);

        FileUtil.createFile(content, arguments.get("savePath") + "/result.json");

        Logger.info(String.format("%s", result.size()));
    }
    /**
     * args.project {string} 项目路径
     * args.savePath {string} json 文件保存地址
     * args.repository {string} mvn 本地仓库路径
     */
    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
