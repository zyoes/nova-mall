package com.example.generator.custom;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.generator.util.CustomFreemarkerUtil;
import freemarker.template.Template;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomCodeGenerator4Module {
    static String groupId = "com.example";
    static String rootProjectName = "cr";
    static String javaVersion = "21";
    static String basePath = "[module]/src/main/java/com/example/[module]/";

    public static void main(String[] args) throws Exception {
        String module = "user";

        basePath = basePath.replace("[module]", module);
        new File(basePath).mkdirs();
        System.out.println("basePath: " + basePath);

        Map<String, Object> data = new HashMap<>();
        data.put("groupId", groupId);
        data.put("rootProjectName", rootProjectName);
        data.put("javaVersion", javaVersion);
        data.put("module", module);
        System.out.println(data);

        Template template = CustomFreemarkerUtil.getTemplateInstance("project/all.ftl");
        String jsonConfig = CustomFreemarkerUtil.generateToString(template, data);
        System.out.println(jsonConfig);
        JSONArray jsonArray = JSONUtil.parseArray(jsonConfig);
        for (int i = 0; i < jsonArray.size(); i++) {
            generate(data, jsonArray.getJSONObject(i));
        }

        // 创建 mapper 子目录，非必须，只是避免主类上的 @MapperScan("...mapper") 注解在 IDEA 编辑器显示红色提醒而已
        new File(basePath + "/mapper").mkdirs();

        // 创建 db/migration 子目录，用来存放后续由 flyway 自动维护的 sql 文件
        new File("[module]/src/main/resources/db/migration".replace("[module]", module)).mkdirs();

        // 创建单元测试目录下的 resources 子目录
        new File("[module]/src/test/resources".replace("[module]", module)).mkdirs();

        System.out.println("========");
        String moduleConfig = """
                1. 最外层 pom.xml 的 <modules> 配置中增加 <module>%s</module> 然后点击右上角出现的 maven 刷新按钮
                """.formatted(module);
        System.out.println(moduleConfig);
        System.out.println("2. 按需修改 src/main/resources/application.properties 中的端口号");
        System.out.println("3. 按需修改 “网关” 的转发配置（以当前多模块项目为例）");
        String gatewayConfig = """
                        - id: %s
                          predicates:
                            - Path=/%s/**
                          filters:
                            - StripPrefix=1
                          uri: http://localhost:该模块配置的端口号
                """.formatted(module, module);
        System.out.println(gatewayConfig);
        System.out.println("4. 按需修改 src/main/resources/application-dev.yml 中的真实配置");
        System.out.println("5. 按需修改 flyway.conf 中的真实配置");
        System.out.println("========");
    }

    private static void generate(Map<String, Object> data, JSONObject map) throws Exception {
        String templateName = map.getStr("templateName");
        CustomFreemarkerUtil.getTemplate("project/" + templateName + ".ftl");
        String targetPath = map.getStr("targetPath").replace(".", "/");
        String targetSuffix = map.getStr("targetSuffix");
        new File(targetPath).mkdirs();
        String targetName = "XxxApplication".equals(templateName) ? StrUtil.upperFirst(data.get("module").toString()) + "Application" : templateName;
        String target = StrUtil.addSuffixIfNot(targetPath, "/") + targetName + targetSuffix;
        CustomFreemarkerUtil.generate(target, data);
        System.out.println("根据模板【" + templateName + "】自动生成 " + target);
    }
}
