/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2020-03-20 14:37
 */
package com.etosun.godone.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Singleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

@Singleton
public class Logger {
    public void message(Object text) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String logText = "";

        try {
            logText = JSON.toJSONString(text, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
        } catch (Exception ignore){
        }
        System.out.printf("[%s][Info] %s\n", format.format(date), logText);
    }
    
    public void message(String format, Object... args) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String message = new Formatter().format(format, args).toString();

        System.out.printf("[%s][Info] %s\n", dateFormat.format(date), message);
    }
}
