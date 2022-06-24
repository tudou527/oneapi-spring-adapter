/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2020-03-20 14:37
 */
package com.godone.meta.utils;

import com.google.inject.Singleton;

import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class Logger {
    public void info(String formatStr, Object... args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.printf("[%s] %s\n", dateFormat.format(new Date()), String.format(formatStr, args));
    }
}
