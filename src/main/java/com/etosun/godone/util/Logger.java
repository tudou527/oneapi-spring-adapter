/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-01-18 下午10:41
 */
package com.etosun.godone.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void info(Object text) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String logText = "";
        if (text instanceof String) {
            logText = (String) text;
        } else {
            try {
                logText = JSON.toJSONString(text, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
            } catch (Exception ignore){
            }
        }
        System.out.print(String.format("[%s][Info] %s\n", format.format(date), logText));
    }
}
