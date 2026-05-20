package com.example.generator.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CustomFreemarkerUtil {

    static String templatePath = "generator/src/main/resources/templates";

    static Template template;

    public static void getTemplate(String ftlName) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDirectoryForTemplateLoading(new File(templatePath));
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_33));
        template = cfg.getTemplate(ftlName);
    }

    public static void generate(String fileName, Map<String, Object> map) throws Exception {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        template.process(map, bw);
        bw.flush();
        fw.close();
    }
}