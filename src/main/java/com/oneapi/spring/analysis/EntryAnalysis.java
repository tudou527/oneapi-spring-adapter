/**
 *
 * @auther xiaoyun
 * @create 2021-02-22 下午8:50
 */
package com.oneapi.spring.analysis;

import com.oneapi.spring.models.*;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 入口文件解析，在 BasicAnalysis 的基础上多了 method 相关的逻辑
 */
public class EntryAnalysis extends BasicAnalysis {
    // 入口函数
    public JavaFileModel analysis(String classPath) {
        super.analysis(classPath);
    
        if (fileModel != null) {
            fileModel.setFileType(ClassTypeEnum.ENTRY);
            // 补全方法（只考虑配 public 方法）
            targetClass.getMethods().forEach(method -> {
                if (method.isPublic()) {
                    fileModel.getClassModel().getMethods().add(analysisMethod(method));
                }
            });
        }
        return fileModel;
    }

    // 解析方法
    private JavaClassMethodModel analysisMethod(JavaMethod method) {
        JavaClassMethodModel javaMethod = new JavaClassMethodModel();
    
        log.info("  method: %s", method.getName());

        javaMethod.setName(method.getName());
        // 描述&注解
        javaMethod.setDescription(classUtil.getDescription(method, fileLines));
        javaMethod.setAnnotations(classUtil.getAnnotation(method.getAnnotations(), fileModel));

        // 入参及类型
        javaMethod.setParameters(getParameters(method));
    
        log.info("    return: %s", method.getReturnType().getGenericValue());
        // 返回值及类型
        JavaActualType methodReturnType = typeAnalysis.get().analysis(method.getReturnType(), fileModel);
        
        javaMethod.setReturnType(methodReturnType);

        return javaMethod;
    }

    // 方法入参
    private ArrayList<JavaMethodParameter> getParameters(JavaMethod method) {
        ArrayList<JavaMethodParameter> paramList = new ArrayList<>();
    
        log.info("    params: ");

        List<JavaParameter> parameters = method.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return paramList;
        }

        parameters.forEach(p -> {
            JavaMethodParameter param = new JavaMethodParameter();

            param.setName(p.getName());

            log.info("      %s: %s", p.getName(), p.getType().getGenericValue());
        
            // 方法入参及类型
            JavaActualType paramType = typeAnalysis.get().analysis(p.getType(), fileModel);
            param.setType(paramType);

            // 参数描述及注解
            param.setDescription(classUtil.getDescription(p, fileLines));
            param.setAnnotations(classUtil.getAnnotation(p.getAnnotations(), fileModel));

            paramList.add(param);
        });

        return paramList;
    }
}
