package com.example.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 SKU 响应。
 * 用于返回商品规格、价格、库存等 SKU 信息。
 */
@Data
public class ProductSkuResponse {
    /**
     * SKU ID。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 所属商品ID。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    /**
     * SKU 唯一编码。
     */
    private String skuCode;

    /**
     * 规格描述。
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

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}
