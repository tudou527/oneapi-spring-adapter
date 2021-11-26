/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-06-02 下午7:40
 */
package com.etosun.godone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.models.JavaFileModel;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class App {
    public static void run(String[] args) throws Exception {
        HashMap<String, String> arguments = new HashMap<>();

        Arrays.asList(args).forEach(arg -> {
            String[] str = arg.split("=");
            arguments.put(str[0], str[1]);
        });

        if (arguments.get("filePath") == null) {
            throw new Exception("参数错误，缺少 filePath 参数");
        }

        // 判断文件是否存在
        File file = new File(arguments.get("filePath"));
        if (!file.exists()) {
            throw new Exception("文件不存在");
        }

        JavaFileModel fileModel = new JavaFileAnalysis().analysis(arguments);

        String logText = JSON.toJSONString(fileModel, SerializerFeature.DisableCircularReferenceDetect);
        System.out.print(logText);
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
