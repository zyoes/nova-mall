package com.example.product.controller;

import com.example.common.response.PageResponse;
import com.example.common.response.R;
import com.example.product.dto.request.ProductListRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品公开接口。
 * 用于用户端浏览上架商品。
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * 分页查询上架商品列表。
     */
    @Operation(summary = "分页查询上架商品列表")
    @GetMapping("/list")
    public R<PageResponse<ProductResponse>> list(@Valid ProductListRequest request) {
        return R.ok(productService.getProductPage(request));
    }

    /**
     * 查询上架商品详情。
     */
    @Operation(summary = "查询上架商品详情")
    @GetMapping("/{id}")
    public R<ProductResponse> detail(@PathVariable Long id) {
        return R.ok(productService.getProductDetail(id));
    }
}
