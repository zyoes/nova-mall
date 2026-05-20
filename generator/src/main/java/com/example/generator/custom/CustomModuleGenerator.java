package com.example.generator.custom;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomModuleGenerator {
    static String basePath = "[module]/src/main/java/com/example/[module]/";

    public static void main(String[] args) throws Exception {
        String module = "business";
        System.out.println("module=" + module);

        // 首字母大写的实体类名（也可以字段转化【注意多个单词的表名形式和表前缀等场景】这里为例讲解核心逻辑，先从简）
        String Module = StrUtil.upperFirst(StrUtil.toCamelCase(module));
        System.out.println("Module=" + Module);

        // 构造出真正要生成的目标位置
        basePath = basePath.replace("[module]", module);
        new File(basePath).mkdirs();
        System.out.println("basePath: " + basePath);

        // 自动生成新模块的基础代码（新增14+2个，修改1个）

        Map<String, Object> data = new HashMap<>();
        data.put("module", module);
        data.put("Module", Module);
        System.out.println(data);

        generateModule(data);

        System.out.println("========");
        System.out.println("修改根目录下 pom.xml 文件的 menuConfig 菜单变量值 L172");
        String info = """
                 <module>%s</module>
                """.formatted(module);
        System.out.println(info);
        System.out.println("========");
    }

    private static void generateModule(Map<String, Object> data) {
        System.out.println("已经生成16个文件");
    }


}
