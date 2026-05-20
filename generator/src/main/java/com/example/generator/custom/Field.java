package com.example.generator.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Field {
    // 字段名：xxx_id
    private String name;

    // 字段名小驼峰：xxxId
    private String nameLowerCamelCase;

    // 字段名大驼峰：XxxId
    private String nameUpperCameCase;

    // 中文名。例：用户
    private String nameCn;

    // 字段类型。例：char(8)
    private String type;

    // java类型。例：String
    private String javaType;

    // 注释
    private String comment;

    // 允许为空
    private Boolean nullAble;

    // 字符串长度
    private Integer length;

    // 是否可被搜索
    private Boolean searchable;

    // 是否有选项。约定表字段的注释形如：【1-选项一、2-选项二、3-选项三】
    private Boolean hasOption;

    // 是否为图片字段。约定表字段的注释包含 "imageColumn" 特殊字符的，代表要在前端使用图片上传组件
    private Boolean imageColumn;

}