package com.example.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应。
 * 用于返回商品列表、商品详情和管理端商品数据。
 */
@Data
public class ProductResponse {
    /**
     * 商品ID。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    /**
     * 商品封面图URL。
     */
    private String coverImage;

    /**
     * 商品详情图URL列表，JSON 字符串。
     */
    private String detailImages;

    /**
     * 商品状态：1-草稿，2-上架，3-下架。
     */
    private Integer status;

    /**
     * 排序值。
     */
    private Integer sort;

    /**
     * 商品下的 SKU 列表。
     */
    private List<ProductSkuResponse> skuList;

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}
