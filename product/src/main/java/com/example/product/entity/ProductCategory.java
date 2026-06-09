package com.example.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品分类实体。
 * 对应 product_category 表，用于维护商品分类树。
 */
@Getter
@Setter
@TableName("product_category")
public class ProductCategory extends BaseEntity {
    /**
     * 分类名称。
     */
    private String name;

    /**
     * 父分类ID；0 表示顶级分类。
     */
    private Long parentId;

    /**
     * 分类层级；1 表示一级分类。
     */
    private Integer level;

    /**
     * 排序值；数值越小越靠前。
     */
    private Integer sort;

    /**
     * 分类图标URL。
     */
    private String icon;
}
