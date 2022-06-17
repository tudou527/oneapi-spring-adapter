package com.godone.test.util.fileUtil;

import com.godone.meta.utils.FileUtil;
import com.godone.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

@DisplayName("fileUtil.findFileList")
public class FindFileListTest {
    
    @InjectMocks
    FileUtil fileUtil;
    
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("返回所有 .java 文件")
    public void getJavaFile() {
        List<String> fileList = fileUtil.findFileList("glob:**/*.java", TestUtil.getBaseDir());
        
        Assertions.assertNotNull(fileList);
    }
    
    @Test
    @DisplayName("返回所有 .md 文件")
    public void getMdFile() {
        List<String> fileList = fileUtil.findFileList("glob:**/*.md", TestUtil.getBaseDir());
        
        Assertions.assertEquals(fileList.size(), 1);
        Assertions.assertTrue(fileList.get(0).contains("com/godone/testSuite/README.md"));
    }
    
    @Test
    @DisplayName("路径错误")
    public void getError() {
        List<String> fileList = fileUtil.findFileList("glob:**/*.md", TestUtil.getBaseDir() +"/notExistDir");
        
        Assertions.assertEquals(fileList.size(), 0);
    }
}
