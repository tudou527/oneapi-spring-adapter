package com.oneapi.spring.test.analysis.basic;

import com.oneapi.spring.analysis.BasicAnalysis;
import com.oneapi.spring.analysis.TypeAnalysis;
import com.oneapi.spring.cache.ReflectCache;
import com.oneapi.spring.cache.ResourceCache;
import com.oneapi.spring.models.JavaFileModel;
import com.oneapi.spring.utils.ClassUtil;
import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.MavenUtil;
import com.google.inject.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("basic.analysis")
public class AnalysisTest {
    @Mock
    FileUtil fileUtil;
    @Mock
    ClassUtil classUtil;
    @Mock
    TypeAnalysis typeAnalysis;
    @Mock
    ResourceCache resourceCache;
    @Mock
    MavenUtil mvnUtil;
    @Mock
    ReflectCache reflectCache;
    @Mock
    Provider<TypeAnalysis> typeAnalysisProvider;
    @InjectMocks
    BasicAnalysis basicAnalysis;

    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("从资源文件解析")
    public void getBuilderReturnNull() {
        JavaFileModel javaFileModel = basicAnalysis.analysis("com.oneapi.spring.testSuite.field.ComplexField");

        Assertions.assertNull(javaFileModel);
    }
  
}
