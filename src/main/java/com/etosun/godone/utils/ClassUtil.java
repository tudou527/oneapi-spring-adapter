/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:44
 */
package com.etosun.godone.utils;

import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaAnnotationModel;
import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.models.JavaFileModel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ClassUtil {
    @Inject
    private RefUtil refUtil;
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;
    @Inject
    private CommonCache commonCache;

    // 注解
    public ArrayList<JavaAnnotationModel> getAnnotation(List<JavaAnnotation> annotations, JavaFileModel hostModel) {
        ArrayList<JavaAnnotationModel> ans = new ArrayList<>();

        if (annotations == null || annotations.isEmpty()) {
            return null;
        }

        // 遍历所有注解
        annotations.forEach(an -> {
            JavaAnnotationModel javaAn = new JavaAnnotationModel();
            String typeName = an.getType().getSimpleName();
            String anSimpleName = typeName.substring(typeName.lastIndexOf(".") + 1);

            // 注解名称（非 classPath）
            javaAn.setName(anSimpleName);
            Optional<String> optionalAnName= hostModel.getImports().stream().filter(str -> str.endsWith(anSimpleName)).findFirst();
            optionalAnName.ifPresent(javaAn::setClassPath);

            Map<String, AnnotationValue> anMap = an.getPropertyMap();
            if (anMap != null) {
                HashMap<String, Object> anProperty = new HashMap<>();
                an.getPropertyMap().forEach((k, v) -> {
                    boolean isArray = false;
                    ArrayList<AnnotationValue> value = new ArrayList<>();
                    // 注解值为数组的情况: @Autowired(name={"a", "b", "c"})
                    if (v instanceof AnnotationValueList) {
                        isArray = true;
                        value.addAll(((AnnotationValueList) v).getValueList());
                    } else {
                        value.add(v);
                    }

                    List<Object> valueList = value.stream().map(val -> {
                        // 值为引用类型Exp: @AppSwitch(level = Switch.Level.p2)
                        if (val instanceof FieldRef) {
                            FieldRef refValue = (FieldRef) val;
                            refUtil.getRef(refValue.getName(), hostModel);
                            return null;
                        }
                        if (val instanceof DefaultJavaParameterizedType) {
                            System.out.println("DefaultJavaParameterizedType: " + val);
                        }

                        if (val instanceof  Constant) {
                            // 其他情况保持值类型不变: @Autowired(value="name", required=false, index=2) 类型应该分别是 string/boolean/int
                            return ((Constant) val).getValue();
                        }

                        return null;
                    }).collect(Collectors.toList());
                    anProperty.put(k, isArray ? valueList : valueList.get(0));
                });
                javaAn.setFields(anProperty);
            }
            ans.add(javaAn);
        });

        return ans;
    }

    // 描述
    public JavaDescriptionModel getDescription(JavaAnnotatedElement javaElement, List<String> fileLines) {
        String comment = javaElement.getComment();

        if (comment == null && javaElement.getLineNumber() > 1) {
            // 尝试按行读取单行注释
            String prevLineStr = fileLines.get(javaElement.getLineNumber() - 2);
            if (prevLineStr.trim().startsWith("//")) {
                comment = prevLineStr.trim().replaceFirst("//", "").trim();
            }
        }

        JavaDescriptionModel desc = new JavaDescriptionModel();
        desc.setText(comment);

        List<DocletTag> tags = javaElement.getTags();
        if (tags != null && tags.size() > 0) {
            HashMap<String, List<String>> commentTag = new HashMap<>();

            tags.forEach(t -> {
                List<String> tagParam = commentTag.get(t.getName());
                if (tagParam == null) {
                    tagParam = new ArrayList<>();
                }
                tagParam.add(t.getValue().trim());
                // 更新 tag
                commentTag.put(t.getName(), tagParam);
            });
            desc.setTag(commentTag);
        }

        return desc;
    }

    // 返回完整的 import 列表
    public List<String> getImports(JavaClass javaClass) {
        List<String> importList = new ArrayList<>();

        javaClass.getSource().getImports().forEach(imp -> {
            if (imp.endsWith(".*")) {
                // 尝试从当前资源文件、JAR 包中匹配导入
                importList.addAll(mvnUtil.getFuzzyImportPackage(imp));
            } else {
                importList.add(imp);
            }
        });
        
        // 根据 java 的导入规则，当前目录下的文件可以不用手动 import，所以这里需要补全当前目录下的其他 class 作为默认导入
        String classDirPath = new File(javaClass.getSource().getURL().getPath()).getParent();
        commonCache.getResource().forEach((classPath, filePath) -> {
            String fileDirPath = new File(filePath).getParent();
            if (classDirPath.equals(fileDirPath)) {
                importList.add(classPath);
            }
        });

        return importList;
    }
    
    /**
     * 返回 class 泛型
     * Exp: class PageResult<T> 将会返回 T
     */
    public ArrayList<JavaActualType> getActualTypeParameters(JavaClass javaClass) {
        List<JavaTypeVariable<JavaGenericDeclaration>> typeParameters = javaClass.getTypeParameters();

        if (typeParameters.size() > 0) {
            ArrayList<JavaActualType> classType = new ArrayList<>();
            typeParameters.forEach(param -> {
                JavaActualType type = new JavaActualType();
                type.setName(param.getName());
        
                classType.add(type);
            });
    
            return classType;
        }
        
        return null;
    }
}
