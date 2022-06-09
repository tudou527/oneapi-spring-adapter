package com.etosun.godone.analysis;

import com.etosun.godone.cache.PendingCache;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.thoughtworks.qdox.type.TypeResolver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TypeAnalysis {
    private JavaType type;
    // type 所在的宿主文件
    private JavaFileModel hostModel;

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
    
        log.info("      analysis type: {}", type.getBinaryName());

        return getType();
    }

    private JavaActualType getType() {
        JavaActualType javaType = new JavaActualType();
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
                if (javaType.getItem() == null) {
                    javaType.setItem(new ArrayList<>());
                }
                
                log.info("      analysis child type: {}", ct.getBinaryName());
                JavaActualType childActualType = typeAnalysis.get().analysis(ct, hostModel);
                // 递归解析每个子类型
                javaType.getItem().add(childActualType);
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
        
        Optional<String> optionalFullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(simpleTypeName)).findFirst();
        optionalFullTypeName.ifPresent(s -> pendingCache.setCache(s));

        return optionalFullTypeName.orElse(null);
    }
}
