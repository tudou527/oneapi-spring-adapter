package com.etosun.godone.test.analysis;

import com.etosun.godone.models.JavaDescriptionModel;
import com.etosun.godone.utils.ClassUtil;
import com.etosun.godone.utils.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@DisplayName("方法")
public class MethodTest {
    @BeforeEach
    public void getBuilder() {
    }

    @Test
    @DisplayName("class 多行注释")
    public void classMultiDescription() {
        Assertions.assertNotNull("");
    }
}
