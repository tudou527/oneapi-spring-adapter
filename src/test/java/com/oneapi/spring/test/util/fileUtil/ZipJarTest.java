package com.oneapi.spring.test.util.fileUtil;

import com.oneapi.spring.utils.FileUtil;
import com.oneapi.spring.test.TestUtil;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;

@DisplayName("fileUtil.unzipJar")
public class ZipJarTest {
    @InjectMocks
    FileUtil fileUtil;
    @BeforeEach
    public void mockBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("解压 zip 包")
    public void normal() {
        String jarFilePath = TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/guice-4.2.3.jar";
        String zipDir = TestUtil.getBaseDir() + "com/oneapi/spring/testSuite/guice-4.2.3";
        
        fileUtil.unzipJar(new File(jarFilePath), zipDir);
        
        File zipFile = new File(zipDir);
        Assertions.assertTrue(zipFile.exists());
        Assertions.assertTrue(zipFile.isDirectory());
        
        File confirmFile = new File(zipDir + "/.oneapi.unzip_success");
        Assertions.assertTrue(confirmFile.exists());
        Assertions.assertTrue(confirmFile.isFile());
    
        try {
            FileUtils.deleteDirectory(zipFile);
        } catch (Exception ignore) {
        }
    }
    
}
