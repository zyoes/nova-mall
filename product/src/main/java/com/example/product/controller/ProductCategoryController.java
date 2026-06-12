package com.example.product.controller;

import com.example.common.response.R;
import com.example.product.dto.response.ProductCategoryResponse;
import com.example.product.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类公开接口。
 * 用于用户端加载分类树。
 */
@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    /**
     * 查询商品分类树。
     */
    @Operation(summary = "查询商品分类树")
    @GetMapping("/tree")
    public R<List<ProductCategoryResponse>> tree() {
        return R.ok(productCategoryService.getCategoryTree());
    }
}
