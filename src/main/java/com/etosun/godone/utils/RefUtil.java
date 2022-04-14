package com.etosun.godone.utils;

import com.etosun.godone.models.JavaFileModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

/**
 * 引用类型分析
 */
@Singleton
public class RefUtil {
    @Inject
    private CommonCache commonCache;
    @Inject
    private MavenUtil mvnUtil;

    public void getRef(String name, JavaFileModel hostModel) {
        String refNameSpace = name.split("\\.")[0];
        // 从 import 匹配
        Optional<String> optionalClassPath = hostModel.getImports().stream().filter(str -> str.endsWith(String.format(".%s", refNameSpace))).findFirst();
        if (!optionalClassPath.isPresent()) {
            return;
        }

        // 从 mvn 中匹配 classPath 对应的类成员
        // mvnUtil.getClassMember(name, optionalClassPath.get());
    }
}
