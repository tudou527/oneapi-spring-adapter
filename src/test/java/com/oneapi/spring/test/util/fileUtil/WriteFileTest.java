package com.oneapi.spring.test.util.fileUtil;

import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.charset.Charset;

@DisplayName("fileUtil.writeFile")
public class WriteFileTest {
    @Mock
    File file;
    @InjectMocks
    FileUtil fileUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("正常写文件")
    public void normal() {
        String saveFilePath = TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/test/WriteTest.json";

        fileUtil.writeFile("class Test {}", saveFilePath, Charset.defaultCharset());
        
        File writeFile = new File(saveFilePath);
        
        // 判断文件是否存在
        Assertions.assertTrue(writeFile.exists());
        // 删除文件和目录
        writeFile.delete();
        writeFile.getParentFile().delete();
    }
    
    @Test
    @DisplayName("文件已存在时先删除再创建")
    public void overwrite() {
        String saveFilePath = TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/test/WriteTest.json";
        
        fileUtil.writeFile("class Test {}", saveFilePath, Charset.defaultCharset());
        fileUtil.writeFile("class Test {}", saveFilePath, Charset.defaultCharset());
        
        File writeFile = new File(saveFilePath);
        
        // 判断文件是否存在
        Assertions.assertTrue(writeFile.exists());
        // 删除文件和目录
        writeFile.delete();
        writeFile.getParentFile().delete();
    }
}
