package com.example.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商品分类保存请求。
 * 用于管理端新增或修改商品分类。
 */
@Data
public class ProductCategoryRequest {
    /**
     * 分类ID；为空表示新增，不为空表示修改。
     */
    private Long id;

    /**
     * 分类名称。
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 父分类ID；0 表示顶级分类。
     */
    private Long parentId = 0L;

    /**
     * 排序值；数值越小越靠前。
     */
    private Integer sort = 0;

    /**
     * 分类图标URL。
     */
    private String icon;
}
