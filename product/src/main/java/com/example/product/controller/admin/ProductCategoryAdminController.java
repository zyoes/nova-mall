package com.example.product.controller.admin;

import com.example.common.response.PageResponse;
import com.example.common.response.R;
import com.example.product.dto.request.ProductCategoryListRequest;
import com.example.product.dto.request.ProductCategoryRequest;
import com.example.product.dto.response.ProductCategoryResponse;
import com.example.product.service.ProductCategoryService;
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
 * 商品分类管理接口。
 * 用于管理端维护商品分类。
 */
@RestController
@RequestMapping("/admin/product/category")
@RequiredArgsConstructor
public class ProductCategoryAdminController {
    private final ProductCategoryService productCategoryService;

    /**
     * 分页查询商品分类列表。
     */
    @Operation(summary = "分页查询商品分类列表")
    @GetMapping("/list")
    public R<PageResponse<ProductCategoryResponse>> list(@Valid ProductCategoryListRequest request) {
        return R.ok(productCategoryService.getCategoryPage(request));
    }

    /**
     * 新增或修改商品分类。
     */
    @Operation(summary = "新增或修改商品分类")
    @PostMapping("/save")
    public R<Boolean> save(@Valid @RequestBody ProductCategoryRequest request) {
        return R.ok(productCategoryService.saveOrUpdateCategory(request));
    }

    /**
     * 删除商品分类。
     */
    @Operation(summary = "删除商品分类")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(productCategoryService.deleteCategory(id));
    }
}
