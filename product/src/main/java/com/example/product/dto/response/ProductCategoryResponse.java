package com.example.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类响应。
 * 用于返回分类列表或分类树节点。
 */
@Data
public class ProductCategoryResponse {
    /**
     * 分类ID。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 分类名称。
     */
    private String name;

    /**
     * 父分类ID。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 分类层级。
     */
    private Integer level;

    /**
     * 排序值。
     */
    private Integer sort;

    /**
     * 分类图标URL。
     */
    private String icon;

    /**
     * 子分类列表，用于组装分类树。
     */
    private List<ProductCategoryResponse> children = new ArrayList<>();

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}
