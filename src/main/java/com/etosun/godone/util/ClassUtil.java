/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:44
 */
package com.etosun.godone.util;

import com.etosun.godone.models.JavaAnnotationModel;
import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.models.JavaActualType;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ClassUtil {
    /**
     * 根据 typeName 推断出完整的类型名称
     */
    public static String completeType(String typeName, List<String> imports) {
        AtomicReference<String> fullTypeName = new AtomicReference<>("");
        imports.forEach(name -> {
            // import a.b.c.{typeName} 的情况
            if (name.endsWith("."+ typeName)) {
                fullTypeName.set(name);
            }
        });

        // 考虑同目录
        

        if (fullTypeName.get().contains(".")) {
            return fullTypeName.get();
        }

        // 可能存在 import a.b.c.* 的情况

        return null;
    }

    /**
      * 解析注解
      */
    public static ArrayList<JavaAnnotationModel> getAnnotation(List<JavaAnnotation> annotations, List<String> imports) {
        ArrayList<JavaAnnotationModel> ans = new ArrayList<>();

        if (annotations == null || annotations.isEmpty()) {
            return null;
        }

        // 遍历所有注解
        annotations.forEach(an -> {
            JavaAnnotationModel javaAn = new JavaAnnotationModel();

            javaAn.setName(an.getType().getFullyQualifiedName());

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
    public static JavaDescriptionModel getDescription(String comment, List<DocletTag> tags) {
        JavaDescriptionModel desc = new JavaDescriptionModel();
        desc.setText(comment);

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

    public static ArrayList<JavaActualType> getClassTypeParameters(JavaClass javaClass) {
        List<JavaTypeVariable<JavaGenericDeclaration>> typeParameters = javaClass.getTypeParameters();

        ArrayList<JavaActualType> classType = new ArrayList<>();
        typeParameters.forEach(param -> {
            JavaActualType type = new JavaActualType();
            type.setName(param.getName());

            classType.add(type);
        });

        return classType;
    }
}