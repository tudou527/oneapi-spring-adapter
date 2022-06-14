package com.godone.test.analysis.basic;

import com.etosun.godone.analysis.BasicAnalysis;
import com.etosun.godone.analysis.TypeAnalysis;
import com.etosun.godone.cache.ReflectCache;
import com.etosun.godone.cache.ResourceCache;
import com.etosun.godone.models.JavaActualType;
import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.etosun.godone.utils.MavenUtil;
import com.godone.test.TestUtil;
import com.google.inject.Provider;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.util.ArrayList;

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
