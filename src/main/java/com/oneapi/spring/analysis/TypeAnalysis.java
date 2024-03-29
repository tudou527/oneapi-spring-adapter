package com.oneapi.spring.analysis;

import com.oneapi.spring.cache.PendingCache;
import com.oneapi.spring.models.ClassTypeEnum;
import com.oneapi.spring.models.JavaActualType;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.utils.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.model.impl.DefaultJavaWildcardType;
import com.thoughtworks.qdox.type.TypeResolver;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeAnalysis {
    private JavaType type;
    // type 所在的宿主文件
    private JavaFileModel hostModel;
    @Inject
    Logger log;
    @Inject
    private PendingCache pendingCache;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;
    
    /**
     * @param type 待解析的类型
     * @param fileModel 宿主对象
     */
    public JavaActualType analysis(JavaType type, JavaFileModel fileModel) {
        this.type = type;
        this.hostModel = fileModel;

        return getType();
    }

    private JavaActualType getType() {
        JavaActualType javaType = new JavaActualType();
        
        // 类型为：? (extends|super) ImmutableCollection
        if (type instanceof DefaultJavaWildcardType) {
            DefaultJavaWildcardType wildType = (DefaultJavaWildcardType) type;
            // 目前只想到了反射这一种方式拿到 extends 的具体类型
            List<JavaType> bounds = (List<JavaType>) ReflectionTestUtils.getField(wildType, "bounds");
            // 暂时只支持解析第一个继承的类型
            if (bounds != null && bounds.size() > 0) {
                this.type = bounds.get(0);
            }
        }
    
        String fullTypeName = type.getFullyQualifiedName();

        if (type instanceof DefaultJavaParameterizedType) {
            DefaultJavaParameterizedType paramType = (DefaultJavaParameterizedType) type;

            String typeName = fullTypeName.substring(fullTypeName.lastIndexOf(".") + 1);
            String typeClassPath = completeTypeClassPath(type, hostModel);
            List<JavaType> childTypeList = paramType.getActualTypeArguments();
            
            // 把 [] 表示的集合转换为 list
            if (paramType.getDimensions() > 0) {
                typeName = "List";
                typeClassPath = "java.util.List";
    
                TypeResolver typeResolver = TypeResolver.byPackageName(
                    hostModel.getJavaSource().getPackageName(),
                    hostModel.getJavaSource().getJavaClassLibrary(),
                    hostModel.getJavaSource().getImports()
                );
    
                DefaultJavaParameterizedType childParamType = new DefaultJavaParameterizedType(
                    paramType.getFullyQualifiedName().replaceAll("\\[\\]", ""),
                    paramType.getName(),
                    paramType.getDimensions() - 1,
                    typeResolver
                );
                childParamType.setActualArgumentTypes(paramType.getActualTypeArguments());

                childTypeList = new ArrayList<JavaType>(){{
                    add(childParamType);
                }};
            }
            
            // 取 className 作为类型名称
            javaType.setName(typeName);
            javaType.setClassPath(typeClassPath);
    
            // 包含子类型，Exp: HashMap<String, List<String>>
            for (JavaType ct: childTypeList) {
                if (javaType.getItems() == null) {
                    javaType.setItems(new ArrayList<>());
                }
    
                if (hostModel.getFileType().equals(ClassTypeEnum.ENTRY)) {
                    log.info("        child type: %s", ct.getGenericValue());
                } else {
                    log.info("      child type: %s", ct.getGenericValue());
                }
    
                JavaActualType childActualType = typeAnalysis.get().analysis(ct, hostModel);
                // 递归解析每个子类型
                javaType.getItems().add(childActualType);
            }
        }
        
        return javaType;
    }
    
    private String completeTypeClassPath(JavaType type, JavaFileModel hostModel) {
        String typeName = type.getBinaryName();
        // 取 typeName 最后一个 . 之后的部分
        String simpleTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        
        if (PendingCache.blackListClassPrefix.stream().anyMatch(typeName::startsWith) || simpleTypeName.length() == 1) {
            return typeName;
        }
        
        // class 中的子类
        if (typeName.contains("$")) {
            pendingCache.setCache(typeName);
            return typeName;
        }
    
        // 从 import 列表中找到匹配的 class 并加入到待解析队列
        Optional<String> optionalFullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(simpleTypeName)).findFirst();
        optionalFullTypeName.ifPresent(s -> pendingCache.setCache(s));
        if (optionalFullTypeName.isPresent()) {
            pendingCache.setCache(optionalFullTypeName.get());
            return optionalFullTypeName.get();
        }

        // 引用同目录下的子类时格式可能是 MainClass.SubClass 形式
        String[] typeNameStr = typeName.split("\\.");
        String mainClass = typeNameStr[typeNameStr.length -2];
        if (mainClass != null) {
            Optional<String> fullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(mainClass)).findFirst();
            if (fullTypeName.isPresent()) {
                pendingCache.setCache(fullTypeName.get());
                return fullTypeName.get() + "$"+ simpleTypeName;
            }
        }

        return null;
    }
}
