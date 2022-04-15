package com.etosun.godone.analysis;

import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.CommonCache;
import com.etosun.godone.utils.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeAnalysis {
    private JavaType type;
    // type 所在的宿主文件
    private JavaFileModel hostModel;

    @Inject
    private Logger logger;
    @Inject
    private CommonCache commonCache;
    @Inject
    Provider<TypeAnalysis> typeAnalysis;
    @Inject
    Provider<BasicAnalysis> baseAnalysis;
    
    /**
     * @param type 待解析的类型
     * @param fileModel 宿主对象
     */
    public JavaActualType analysis(JavaType type, JavaFileModel fileModel) {
        this.type = type;
        this.hostModel = fileModel;
    
        logger.message("      analysis type: %s", type.getBinaryName());

        return getType();
    }
    
    private JavaActualType getType() {
        JavaActualType javaType = new JavaActualType();
        String fullTypeName = type.getFullyQualifiedName();
        // 取 className 作为类型名称
        javaType.setName(fullTypeName.substring(fullTypeName.lastIndexOf(".") + 1));
        javaType.setClassPath(completeTypeClassPath(type, hostModel));

        // 包含子类型，Exp: HashMap<String, List<String>>
        if (type instanceof DefaultJavaParameterizedType) {
            // 子类型
            List<JavaType> childTypeList = ((DefaultJavaParameterizedType) type).getActualTypeArguments();
            for (JavaType ct: childTypeList) {
                if (javaType.getItem() == null) {
                    javaType.setItem(new ArrayList<>());
                }
                
                if (ct.getFullyQualifiedName().endsWith(javaType.getName())) {
                    // 递归解析每个子类型
                    javaType.getItem().add(javaType);
                } else {
                    logger.message("      analysis child type: %s", ct.getBinaryName());
                    JavaActualType childActualType = typeAnalysis.get().analysis(ct, hostModel);
                    // 递归解析每个子类型
                    javaType.getItem().add(childActualType);
                }
            }
        }
        
        return javaType;
    }
    
    private String completeTypeClassPath(JavaType type, JavaFileModel hostModel) {
        String typeName = type.getBinaryName();
        // 取 typeName 最后一个 . 之后的部分
        String simpleTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        List<String> startsWithBlackList = new ArrayList<String>() {{
            add("java.");
            add("String");
            add("javax.");
            add("void");
            add("org.springframework.");
            add("org.slf4j.");
        }};
        if (startsWithBlackList.stream().anyMatch(typeName::startsWith) || simpleTypeName.length() == 1) {
            return typeName;
        }
        
        Optional<String> optionalFullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(simpleTypeName)).findFirst();
        optionalFullTypeName.ifPresent(s -> commonCache.savePaddingClassPath(s));

        return optionalFullTypeName.orElse(null);
    }
}
