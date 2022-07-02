package com.oneapi.spring.test.util.fileUtil;

import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.utils.Logger;
import com.oneapi.spring.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

@DisplayName("fileUtil.exec")
public class ExecTest {
    @Mock
    Logger log;
    @InjectMocks
    FileUtil fileUtil;
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);

        Mockito.doNothing().when(log).info(Mockito.any(), Mockito.any());
    }
    
    @Test
    @DisplayName("执行命令行")
    public void normal() {
        File execFile = new File(TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/exec.text");
    
        String command = "echo \"\" > " + execFile.getAbsolutePath();
        fileUtil.exec(command, TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/");

        Assertions.assertTrue(execFile.exists());
        // 传出用过命令行创建的文件
        execFile.delete();
    }
}
