package com.example.generator.custom;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.generator.util.CustomFreemarkerUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CustomCodeGenerator {
    static String basePath = "[module]/src/main/java/com/example/[module]/";

    static String frontendType = "frontend-admin";
    static String apiJsPath = frontendType + "/src/api/[module]/";

    static String vuePath = frontendType + "/src/views/[module]/";
    static boolean readOnly = false;

    public static void main(String[] args) throws Exception {
        String module = "business";
        System.out.println("module=" + module);

        // 构造出真正要生成的目标位置
        basePath = basePath.replace("[module]", module);
        new File(basePath).mkdirs();
        System.out.println("basePath: " + basePath);

        // 表名、对应实体类名
        String tableName = "confirm_order";
        // 首字母大写的实体类名（也可以字段转化【注意多个单词的表名形式和表前缀等场景】这里为例讲解核心逻辑，先从简）
        String Domain = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
        System.out.println("表名：" + tableName + "，对应的实体名：" + Domain);
        // 小驼峰格式的 domain = (配置文件中配置的实体名，首字母改成小些，如 oneTwo)
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        System.out.println("表名的小驼峰形式：" + domain);
        // 中划线格式的 do-main，常用于 url 路径
        // 这里以表名为基础进行修改【如果有表前缀，记得先替换前缀】
        String doHyphenMain = tableName.replaceAll("_", "-");
        System.out.println("表名的中划线格式：" + doHyphenMain);

        // 连接数据库
        initDb();

        // 表字段
        List<Field> fieldList = CustomDbUtil.getColumnByTableName(tableName);

        // 表注释
        String tableNameCn = CustomDbUtil.getTableComment(tableName);

        // 涉及到的 Java 类型
        Set<String> typeSet = getJavaType(fieldList);
        System.out.println(typeSet);

        Map<String, Object> data = new HashMap<>();
        data.put("module", module);
        data.put("tableName", tableName);
        data.put("tableNameCn", tableNameCn);
        data.put("Domain", Domain);
        data.put("domain", domain);
        data.put("doHyphenMain", doHyphenMain);
        data.put("fieldList", fieldList);
        data.put("typeSet", typeSet);
        data.put("readOnly", readOnly);
        data.put("frontendType", frontendType);
        data.put("hasImageColumn", fieldList.stream().anyMatch(Field::getImageColumn));
        System.out.println(data);

        // 自动生成 Entity
        generate(Domain, data, "entity", "entity");

        // 自动生成 Mapper
        generate(Domain, data, "mapper", "mapper");

        // 自动生成 XxxListRequest
        generate(Domain, data, "dto/request", "listRequest");

        // 自动生成 XxxResponse
        generate(Domain, data, "dto/response", "response");

        // 自动生成 XxxService
        generate(Domain, data, "service", "service");

        // 自动生成 XxxServiceImpl
        generate(Domain, data, "service/impl", "serviceImpl");

        if (frontendType.endsWith("admin")) {
            // 自动生成 XxxAdminController
            generate(Domain, data, "controller/admin", "adminController");

            // 自动生成 xxxAdmin.js
            generateApiJs(domain, data, "apiAdminJs");
        } else {
            // 自动生成 XxxController
            generate(Domain, data, "controller", "controller");

            // 自动生成 xxx.js
            generateApiJs(domain, data, "apiJs");
        }

        // 自动生成 xxxVue
        generateVue(Domain, data);

        // 自动生成 XxxRequest
        generate(Domain, data, "dto/request", "request");

        System.out.println("========");
        System.out.println("修改 AppLayout.vue 文件的 menuConfig 菜单变量值 L172");
        String menu = """
                {
                    path: '/%s',
                    title: '%s',
                    icon: Avatar // <= 替换图标
                }
                """.formatted(doHyphenMain, tableNameCn);
        System.out.println(menu);
        System.out.println("--------");
        System.out.println("修改 index.js 文件的路由配置");
        String router = """
                {
                    path: '%s',
                    name: '%s',
                    component: () => import('@/views/%s/%s.vue'),
                    meta: { title: '%s' }
                }
                """.formatted(doHyphenMain, Domain, module, Domain, tableNameCn);
        System.out.println(router);
        System.out.println("========");
    }

    private static void generate(String Domain, Map<String, Object> data, String packageName, String templateName) throws Exception {
        CustomFreemarkerUtil.getTemplate(templateName + ".ftl");
        String Target = templateName.substring(0, 1).toUpperCase() + templateName.substring(1);
        String targetPath = basePath + packageName + "/";
        new File(targetPath).mkdirs();
        String typeClass = targetPath + Domain + ("entity".equals(templateName) ? "" : Target) + ".java";
        CustomFreemarkerUtil.generate(typeClass, data);
        System.out.println("根据模板【" + templateName + "】自动生成 " + Target + " 完成，" + typeClass);
    }

    private static void generateApiJs(String domain, Map<String, Object> data, String templateName) throws Exception {
        CustomFreemarkerUtil.getTemplate(templateName + ".ftl");
        apiJsPath = apiJsPath.replace("[module]", String.valueOf(data.get("module")));
        new File(apiJsPath).mkdirs();
        String fileName = apiJsPath + domain + ".js";
        System.out.println("自动生成 " + fileName + " 完成");
        CustomFreemarkerUtil.generate(fileName, data);
    }

    private static void generateVue(String Domain, Map<String, Object> data) throws Exception {
        CustomFreemarkerUtil.getTemplate("vue.ftl");
        vuePath = vuePath.replace("[module]", String.valueOf(data.get("module")));
        new File(vuePath).mkdirs();
        String fileName = vuePath + Domain + ".vue";
        System.out.println("自动生成 " + fileName + " 完成");
        CustomFreemarkerUtil.generate(fileName, data);
    }

    private static void initDb() {
        System.out.println("读取数据库配置文件 - 开始");
        Properties dbProperties = new Properties();
        File file = getFile("db.properties");
        try {
            dbProperties.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = dbProperties.getProperty("url");
        String username = dbProperties.getProperty("username");
        String password = dbProperties.getProperty("password");
        System.out.println("url=" + url);
        System.out.println("username=" + username);
        System.out.println("password=******");
        System.out.println("读取数据库配置文件 - 完成");

        CustomDbUtil.url = url;
        CustomDbUtil.username = username;
        CustomDbUtil.password = password;
        CustomDbUtil.database = ReUtil.getGroup1(".*:\\d+/([\\w\\d-_]+)\\?*.*", url); // 根据实际情况按需调整
    }


    private static File getFile(String fileName) {
        ClassLoader classLoader = CodeGenerator.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) {
            throw new IllegalArgumentException("file is not found: [" + fileName + "]");
        } else {
            return new File(resource.getFile());
        }
    }

    /**
     * 获得所有需要被 import 的 Java 类型，通过 Set 自动去重复
     *
     * @param fieldList 字段列表
     */
    static Set<String> getJavaType(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        fieldList.forEach(field -> set.add(field.getJavaType()));
        return set;
    }
}
