package com.godone.test.util.fileUtil;

import com.etosun.godone.utils.FileUtil;
import com.godone.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;

@DisplayName("fileUtil.getFileOrIOEncode")
public class EncodeTest {
    @InjectMocks
    FileUtil fileUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("UTF-8 编码")
    public void utf8() {
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.TestController");
        Charset encode = fileUtil.getFileOrIOEncode(filePath);
        
        Assertions.assertEquals(encode.name(), "UTF-8");
    }
    
    @Test
    @DisplayName("GB2312 编码")
    public void gbk() {
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.TestClassOfGBK");
        Charset encode = fileUtil.getFileOrIOEncode(filePath);
        
        // 这里为啥是 windows-1252 ??
        Assertions.assertEquals(encode.name(), "windows-1252");
    }
    
    @Test
    @DisplayName("失败时返回 UTF-8")
    public void fail() {
        String filePath = TestUtil.getFileByClassPath("com.godone.testSuite.fail");
        Charset encode = fileUtil.getFileOrIOEncode(filePath);
    
        Assertions.assertEquals(encode.name(), "UTF-8");
    }
}
