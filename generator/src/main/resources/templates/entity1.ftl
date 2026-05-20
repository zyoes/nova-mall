package com.example.${module}.entity;

// 表名；${tableName}
// tableNameCn；${tableNameCn}
// Domain；${Domain}
// doHyphenMain；${doHyphenMain}
// domain；${domain}

<#list fieldList as field>
    // private ${field.javaType} ${field.name} // ${field.comment}
</#list>