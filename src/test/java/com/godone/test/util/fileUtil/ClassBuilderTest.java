package com.godone.test.util.fileUtil;

import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
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
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.TestController");
        JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
    
        Assertions.assertNotNull(builder);
    }
    
    @Test
    @DisplayName("文件不存在时返回  null")
    public void fail() {
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.fail");
        JavaProjectBuilder builder = fileUtil.getBuilder(filePath);
    
        Assertions.assertNull(builder);
    }
}
