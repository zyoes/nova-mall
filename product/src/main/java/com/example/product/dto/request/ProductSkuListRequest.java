package com.example.product.dto.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品 SKU 列表查询请求。
 * 用于按商品、关键字、状态分页查询 SKU。
 */
@Data
public class ProductSkuListRequest extends PageRequest {
    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "SKU状态：1-有效，0-无效")
    private Integer status;
}
