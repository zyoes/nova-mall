package com.example.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品 SKU 保存请求。
 * 用于管理端新增或修改某个商品的 SKU。
 */
@Data
public class ProductSkuRequest {
    /**
     * SKU ID；为空表示新增，不为空表示修改。
     */
    private Long id;

    /**
     * 所属商品ID。
     */
    private Long productId;

    /**
     * SKU 唯一编码。
     */
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;

    /**
     * 规格描述，例如“红色/128G”。
     */
    private String spec;

    /**
     * 销售价格。
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能小于0")
    private BigDecimal price;

    /**
     * 库存数量。
     */
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock = 0;

    /**
     * SKU 图片URL。
     */
    private String image;

    /**
     * SKU 状态：1-有效，0-无效。
     */
    private Integer status = 1;
}
