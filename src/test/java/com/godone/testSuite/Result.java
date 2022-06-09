package com.godone.testSuite;

import java.util.HashMap;

/**
 * MVC 统一响应
 *
 * @author amyxia
 * @date Jul 17, 2019 11:38:42 PM
 */
public class Result<T> {
    // byte 应该被识别为字符串
    private Boolean success;
    private HashMap<String, String> mapData;

    /**
     * 响应消息
     */
    private String errorMessage;

    /**
     * 响应数据
     */
    private T data;
}
