package com.example.product.controller.admin;

import com.example.common.response.PageResponse;
import com.example.common.response.R;
import com.example.product.dto.request.ProductListRequest;
import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理接口。
 * 用于管理端维护商品 SPU。
 */
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;

    /**
     * 分页查询商品列表。
     */
    @Operation(summary = "分页查询商品列表")
    @GetMapping("/list")
    public R<PageResponse<ProductResponse>> list(@Valid ProductListRequest request) {
        return R.ok(productService.getAdminProductPage(request));
    }

    /**
     * 新增或修改商品。
     */
    @Operation(summary = "新增或修改商品")
    @PostMapping("/save")
    public R<Boolean> save(@Valid @RequestBody ProductRequest request) {
        return R.ok(productService.saveOrUpdateProduct(request));
    }

    /**
     * 删除商品。
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(productService.deleteProduct(id));
    }
}
