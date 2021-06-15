/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:44
 */
package com.etosun.godone.util;

import com.etosun.godone.models.Annotation;
import com.etosun.godone.models.Description;
import com.etosun.godone.models.JavaActualType;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ClsUtil {
    /**
     * 根据 typeName 推断出完整的类型名称
     */
    public static String completeType(String typeName, List<String> imports) {
        AtomicReference<String> fullTypeName = new AtomicReference<>("");
        imports.forEach(name -> {
            if (name.endsWith("."+ typeName)) {
                fullTypeName.set(name);
            }
        });

        if (fullTypeName.get().contains(".")) {
            return fullTypeName.get();
        }

        return null;
    }

    /**
      * 解析注解
      */
    public static ArrayList<Annotation> getAnnotation(List<JavaAnnotation> annotations, List<String> imports) {
        ArrayList<Annotation> ans = new ArrayList<Annotation>();

        if (annotations == null) {
            return null;
        }

        // 遍历所有注解
        annotations.forEach(an -> {
            Annotation javaAn = new Annotation();

            // 需要补全注解的完整类型
            String anClassName = an.getType().getName();
            javaAn.setName(anClassName);
            javaAn.setClassPath(completeType(anClassName, imports));

            Map<String, AnnotationValue> anMap = an.getPropertyMap();

            if (anMap != null) {
                HashMap<String, Object> anProperty = new HashMap<>();
                an.getPropertyMap().forEach((k, v) -> {
                    Object value = v.getParameterValue().toString().replaceAll("^\"|\"$", "");

                    if (value.toString().contains(".")) {
                        String[] clsName = StringUtils.split(value.toString(), ".");
                        assert clsName != null;
                        String fullClsName = completeType(clsName[0], imports);

                        if (fullClsName != null) {
                            value = fullClsName +"."+ clsName[1];
                        }
                    }

                    if (v instanceof AnnotationValueList) {
                        value = new ArrayList<>();
                        Object finalValue = value;
                        ((AnnotationValueList) v).getValueList().forEach(val -> {
                            ((ArrayList) finalValue).add(val.toString().replaceAll("^\"|\"$", ""));
                        });
                    }
                    anProperty.put(k, value);
                });

                javaAn.setFields(anProperty);
            }

            ans.add(javaAn);
        });

        return ans;
    }

    /**
     * 解析注释
     */
    public static Description getDescription(String comment, List<DocletTag> tags) {
        Description desc = new Description();
        desc.setComment(comment);

        if (tags != null) {
            tags.forEach(tag -> {
                if (desc.getTag() == null) {
                    desc.setTag(new HashMap<>());
                }
                desc.getTag().put(tag.getName(), tag.getValue().trim());
            });
        }

        return desc;
    }

    /**
     * 解析类型
     */
    public static JavaActualType getType(JavaType type, List<String> imports) {
        JavaActualType javaType = new JavaActualType();

        javaType.setName(type.getFullyQualifiedName());

        // 包含子类型，Exp: HashMap<String, List<String>>
        if (type instanceof DefaultJavaParameterizedType) {

            // 子类型
            List<JavaType> childTypeList = ((DefaultJavaParameterizedType) type).getActualTypeArguments();

            for (JavaType ct: childTypeList) {
                if (javaType.getItem() == null) {
                    javaType.setItem(new ArrayList<>());
                }
                // 递归解析每个子类型
                javaType.getItem().add(getType(ct, imports));
            }
        }

        return javaType;
    }
}
