package com.godone.test.analysis.basic;

import com.godone.meta.analysis.BasicAnalysis;
import com.godone.meta.analysis.TypeAnalysis;
import com.godone.meta.cache.ReflectCache;
import com.godone.meta.cache.ResourceCache;
import com.godone.meta.models.JavaFileModel;
import com.godone.meta.utils.ClassUtil;
import com.godone.meta.utils.FileUtil;
import com.godone.meta.utils.MavenUtil;
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
        JavaFileModel javaFileModel = basicAnalysis.analysis("com.godone.testSuite.field.ComplexField");

        Assertions.assertNull(javaFileModel);
    }
  
}
