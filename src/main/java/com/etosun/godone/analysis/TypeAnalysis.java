package com.etosun.godone.analysis;

import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.CommonCache;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.Logger;
import com.etosun.godone.utils.MavenUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TypeAnalysis {
    private JavaType type;
    // type 所在的宿主文件
    private JavaFileModel hostModel;
    
    @Inject
    private MavenUtil mvnUtil;
    @Inject
    private FileUtil fileUtil;
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
            add("javax.");
            add("void");
            add("org.springframework.");
            add("org.slf4j.");
        }};
        if (startsWithBlackList.stream().anyMatch(typeName::startsWith) || simpleTypeName.length() == 1) {
            return typeName;
        }
        
        Optional<String> optionalFullTypeName = hostModel.getImports().stream().filter(str -> str.endsWith(simpleTypeName)).findFirst();
        if (optionalFullTypeName.isPresent()) {
            if (commonCache.getPaddingClassPath(optionalFullTypeName.get()) == null) {
                commonCache.savePaddingClassPath(optionalFullTypeName.get(), 1);
            }
        }

        return optionalFullTypeName.orElse(null);
    }
    
    // 解析类型对应的文件
    private void analysisTypeClass(String classPath) {
        if (commonCache.getModel(classPath) != null) {
            return;
        }
        
        String classFilePath = commonCache.getResource().get(classPath);
        if (classFilePath != null) {
            JavaFileModel fileModel = baseAnalysis.get().analysis(classFilePath);
            commonCache.saveModel(fileModel.getClassModel().getClassPath(), fileModel);
            return;
        }

        // 尝试判断 classPath 是否来自 JAR 包
        String jarFilePath = commonCache.getReflectClass().get(classPath);
        if (jarFilePath == null) {
            return;
        }

        // 判断源码 JAR 是否存在
        File sourceJar = new File(jarFilePath.replace(".jar", "-sources.jar"));
        if (sourceJar.exists()) {
            String zipDir = sourceJar.getParent() + "/source";
            // 解压源码JAR
            fileUtil.unzipJar(sourceJar, zipDir);
            // 缓存解压的资源文件
            mvnUtil.saveResource(zipDir, false);
        
            // 重新调用自己（会走到第一个分支逻辑中）
            analysisTypeClass(classPath);
            return;
        }

        // 尝试从 JAR 包中解析类型
        JavaFileModel fileModel = mvnUtil.builderFromReflectClass(classPath, jarFilePath);
        commonCache.saveModel(classPath, fileModel);
    }
}
