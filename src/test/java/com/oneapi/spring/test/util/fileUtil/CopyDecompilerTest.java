package com.oneapi.spring.test.util.fileUtil;

import com.oneapi.spring.utils.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;

@DisplayName("fileUtil.copyDecompiler")
public class CopyDecompilerTest {
    @InjectMocks
    FileUtil fileUtil;
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("复制反编译 jar")
    public void normal() {
        String currentFilePath = fileUtil.getCurrentDir();
        fileUtil.copyDecompiler();
        
        File compilerFile = new File(currentFilePath + "/lib/procyon-decompiler.jar");
        File parentFile = new File(currentFilePath + "/lib");
        Assertions.assertTrue(compilerFile.exists());
    
        compilerFile.delete();
        parentFile.delete();
    }
}
