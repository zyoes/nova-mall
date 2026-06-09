package com.example.product.dto.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品列表查询请求。
 * 用于公开端和管理端分页查询商品。
 */
@Data
public class ProductListRequest extends PageRequest {
    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商品状态：1-草稿，2-上架，3-下架")
    private Integer status;
}
