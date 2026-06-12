package com.example.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品实体。
 * 对应 product 表，表示一个商品 SPU。
 */
@Getter
@Setter
@TableName("product")
public class Product extends BaseEntity {
    /**
     * 商品名称。
     */
    private String name;

    /**
     * 商品描述。
     */
    private String description;

    /**
     * 所属分类ID。
     */
    private Long categoryId;

    /**
     * 商品封面图URL。
     */
    private String coverImage;

    /**
     * 商品详情图URL列表，按 JSON 字符串存储。
     */
    private String detailImages;

    /**
     * 商品状态：1-草稿，2-上架，3-下架。
     */
    private Integer status;

    /**
     * 排序值；数值越小越靠前。
     */
    private Integer sort;
}
