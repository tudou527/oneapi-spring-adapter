package com.etosun.godone.analysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etosun.godone.models.JavaFileModel;
import com.etosun.godone.util.FileUtil;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;

public class Project {
    private final String savePath;
    private final String projectPath;

    public Project(CommandLine cmd) {
        savePath = cmd.getOptionValue("o");
        projectPath = cmd.getOptionValue("p");
    }

    public void getSchema() {
        ArrayList<JavaFileModel> fileModels = new ArrayList<>();
        FileUtil.findFileList("glob:**/*.java", projectPath).forEach(file -> {
            try {
                JavaFileModel fileModel = new JavaFile(file).analysis();
                fileModels.add(fileModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        String result = JSON.toJSONString(fileModels, SerializerFeature.DisableCircularReferenceDetect);
        FileUtil.writeFile(result, savePath + "/result.json", null);

        System.out.printf("found %s files%n", fileModels.size());
    }
}
