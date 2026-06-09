package com.example.product.dto.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品分类列表查询请求。
 * 用于按关键字、父分类、层级查询分类数据。
 */
@Data
public class ProductCategoryListRequest extends PageRequest {
    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类层级")
    private Integer level;
}
