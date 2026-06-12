package com.example.product.dto.request;

import com.example.common.constant.StatusConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 商品保存请求。
 * 用于管理端新增或修改商品 SPU，可携带 SKU 列表一起保存。
 */
@Data
public class ProductRequest {
    /**
     * 商品ID；为空表示新增，不为空表示修改。
     */
    private Long id;

    /**
     * 商品名称。
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品描述，支持富文本或长文本。
     */
    private String description;

    /**
     * 所属分类ID。
     */
    @NotNull(message = "商品分类不能为空")
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
    private Integer status = StatusConstant.PRODUCT_STATUS_DRAFT;

    /**
     * 排序值；数值越小越靠前。
     */
    private Integer sort = 0;

    /**
     * 商品下的 SKU 列表。
     */
    @Valid
    @NotEmpty(message = "商品至少需要一个 SKU")
    private List<ProductSkuRequest> skuList;
}
