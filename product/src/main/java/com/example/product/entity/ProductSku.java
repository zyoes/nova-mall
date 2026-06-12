package com.example.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 商品 SKU 实体。
 * 对应 product_sku 表，表示商品的具体规格、价格和库存。
 */
@Getter
@Setter
@TableName("product_sku")
public class ProductSku extends BaseEntity {
    /**
     * 所属商品ID。
     */
    private Long productId;

    /**
     * SKU 唯一编码。
     */
    private String skuCode;

    /**
     * 规格描述，例如“红色/128G”。
     */
    private String spec;

    /**
     * 销售价格。
     */
    private BigDecimal price;

    /**
     * 库存数量。
     */
    private Integer stock;

    /**
     * SKU 图片URL。
     */
    private String image;

    /**
     * SKU 状态：1-有效，0-无效。
     */
    private Integer status;
}
