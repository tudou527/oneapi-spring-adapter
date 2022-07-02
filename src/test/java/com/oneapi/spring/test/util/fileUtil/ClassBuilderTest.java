package com.oneapi.spring.test.util.fileUtil;

import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.test.TestUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


@DisplayName("fileUtil.getBuilder")
public class ClassBuilderTest {
    @InjectMocks
    FileUtil fileUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("返回 JavaProjectBuilder")
    public void utf8() {
        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.TestController");
        JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
    
        Assertions.assertNotNull(builder);
    }
    
    @Test
    @DisplayName("文件不存在时返回  null")
    public void fail() {
        String filePath = TestUtil.getFileByClassPath("com.oneapi.spring.testSuite.fail");
        JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
    
        Assertions.assertNull(builder);
    }
}
