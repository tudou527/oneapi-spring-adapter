package com.godone.test.util.fileUtil;

import com.godone.meta.utils.FileUtil;
import com.godone.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;

@DisplayName("fileUtil.exec")
public class ExecTest {
    @InjectMocks
    FileUtil fileUtil;
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("执行命令行")
    public void normal() {
        File execFile = new File(TestUtil.getBaseDir() + "com/godone/testSuite/exec.text");
    
        String command = "echo \"\" > " + execFile.getAbsolutePath();
        fileUtil.exec(command, TestUtil.getBaseDir() + "com/godone/testSuite/");

        Assertions.assertTrue(execFile.exists());
        // 传出用过命令行创建的文件
        execFile.delete();
    }
}
