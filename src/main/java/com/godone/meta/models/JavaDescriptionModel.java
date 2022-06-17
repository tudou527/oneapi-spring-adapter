/**
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午6:59
 */
package com.godone.meta.models;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 注释
 */
@Data
public class JavaDescriptionModel implements Serializable {
    // 注释内容
    @JSONField(ordinal = 0)
    String text;

    /**
     * 注释中的 tag 信息，Exp: @Description 后端
     * 考虑回存在 @param 标签会存在多个，所以 tag 后的值为数组，Exp:
     * \@param deptNo    部门编号
     * \@param layer     组织层级,可选
     * \@param subDept   下属组织,可选,如果有值则展现 subDept 为根的图谱
     * 对应的存储格式为：
     * {
     *     param: [
     *      "deptNo    部门编号",
     *      "layer     组织层级,可选",
     *      "subDept   下属组织,可选,如果有值则展现 subDept 为根的图谱"
     *     ]
     * }
     */
    @JSONField(ordinal = 1)
    HashMap<String, List<String>> tag = new HashMap<>();
}
