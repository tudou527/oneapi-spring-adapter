/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午6:59
 */
package com.etosun.godone.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;

/**
 * 注释
 */
@Data
public class Description {
    // 注释
    @JSONField(ordinal = 1)
    String comment;

    /**
     * 注释中的 tag 信息，Exp:
     *  \@Description 后端
     */
    @JSONField(ordinal = 2)
    HashMap<String, String> tag;
}
