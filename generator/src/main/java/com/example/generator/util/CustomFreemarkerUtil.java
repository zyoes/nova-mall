package com.example.generator.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
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

    public static Template getTemplateInstance(String ftlName) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDirectoryForTemplateLoading(new File(templatePath));
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_33));
        return cfg.getTemplate(ftlName);
    }

    public static String generateToString(Template template, Map<String, Object> map) throws Exception {
        StringWriter out = new StringWriter();
        template.process(map, out);
        return out.toString();
    }
}