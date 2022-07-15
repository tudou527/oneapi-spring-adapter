/**
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:44
 */
package com.oneapi.spring.utils;

import com.google.inject.Provider;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.cache.ResourceCache;
import com.google.inject.Inject;
import com.oneapi.spring.models.*;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.*;
import com.thoughtworks.qdox.model.impl.DefaultJavaAnnotation;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;

@Singleton
public class ClassUtil {
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private PendingCache pendingCache;
    @Inject
    private ResourceCache resourceCache;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;

    // 注解
    public ArrayList<JavaAnnotationModel> getAnnotation(List<JavaAnnotation> annotations, JavaFileModel hostModel) {
        ArrayList<JavaAnnotationModel> ans = new ArrayList<>();

        if (annotations == null || annotations.isEmpty()) {
            return ans;
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
                // 保存注解字段
                ArrayList<JavaAnnotationField> anFieldList = new ArrayList<>();
                
                an.getPropertyMap().forEach((k, v) -> {
                    boolean valueIsArray = false;
                    ArrayList<AnnotationValue> value = new ArrayList<>();
                    // 注解值为数组的情况: @Autowired(name={"a", "b", "c"})
                    if (v instanceof AnnotationValueList) {
                        valueIsArray = true;
                        value.addAll(((AnnotationValueList) v).getValueList());
                    } else {
                        value.add(v);
                    }
    
                    JavaAnnotationField anField = new JavaAnnotationField();
                    anField.setName(k);
        
                    // 先把注解值定义为 hashMap
                    ArrayList<Object> anPropertyValue = new ArrayList<>();
                    value.forEach(val -> {
                        // 值为引用类型Exp: @AppSwitch(level = Switch.Level.p2)
                        if (val instanceof FieldRef) {
                            FieldRef refValue = (FieldRef) val;
                            String refClassName = refValue.getName().split("\\.")[0];
                            Optional<String> optionalFullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(refClassName)).findFirst();
    
                            if (optionalFullTypeName.isPresent()) {
                                // 保存类型
                                anField.setType(optionalFullTypeName.get());
                                // 把引用类型加入到待解析队列
                                pendingCache.setCache(optionalFullTypeName.get());
                            }
    
                            anPropertyValue.add(refValue.getName());
                        }
                        
                        // 值为 class Exp: @AppSwitch(level = UserConfig.class)
                        if (val instanceof TypeRef) {
                            JavaType valType = ((TypeRef) val).getType();
                            JavaActualType childActualType = typeAnalysis.get().analysis(valType, hostModel);
                            anPropertyValue.add(childActualType.getClassPath());
                        }
    
                        // 值为其他注解 Exp: @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "")})
                        if (val instanceof DefaultJavaAnnotation) {
                            ArrayList<JavaAnnotationModel> childAns = getAnnotation(new ArrayList<JavaAnnotation>(){{
                                add((JavaAnnotation)val);
                            }}, hostModel);
                            if (childAns.size() > 0) {
                                anPropertyValue.add(childAns.get(0));
                            }
                        }

                        if (val instanceof Constant) {
                            // 保存类型
                            anField.setType("Constant");
                            // 其他情况保持值类型不变: @Autowired(value="name", required=false, index=2) 类型应该分别是 string/boolean/int
                            anPropertyValue.add(((Constant) val).getValue());
                        }
                    });

                    if (!valueIsArray) {
                        anField.setArray(false);
                        // 原始属性不是数组，并且属性值不是引用类型
                        anField.setValue(anPropertyValue.get(0));
                    } else {
                        anField.setArray(true);
                        anField.setValue(anPropertyValue);
                    }
    
                    anFieldList.add(anField);
                });
                javaAn.setFields(anFieldList);
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
            String prevLineStr = "";
            int commentLine = javaElement.getLineNumber() - 2;
            if (fileLines.size() - 1 >= commentLine ) {
                prevLineStr = fileLines.get(commentLine);
            }
            
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
    
        resourceCache.getCache().forEach((classPath) -> {
            String fileDirPath = new File(resourceCache.getCache(classPath)).getParent();
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
