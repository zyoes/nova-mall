package com.example.product.controller.admin;

import com.example.common.response.PageResponse;
import com.example.common.response.R;
import com.example.product.dto.request.ProductSkuListRequest;
import com.example.product.dto.request.ProductSkuRequest;
import com.example.product.dto.response.ProductSkuResponse;
import com.example.product.service.ProductSkuService;
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
 * 商品 SKU 管理接口。
 * 用于管理端维护商品 SKU。
 */
@RestController
@RequestMapping("/admin/product/sku")
@RequiredArgsConstructor
public class ProductSkuAdminController {
    private final ProductSkuService productSkuService;

    /**
     * 分页查询 SKU 列表。
     */
    @Operation(summary = "分页查询 SKU 列表")
    @GetMapping("/list")
    public R<PageResponse<ProductSkuResponse>> list(@Valid ProductSkuListRequest request) {
        return R.ok(productSkuService.getSkuPage(request));
    }

    /**
     * 新增或修改 SKU。
     */
    @Operation(summary = "新增或修改 SKU")
    @PostMapping("/save")
    public R<Boolean> save(@Valid @RequestBody ProductSkuRequest request) {
        return R.ok(productSkuService.saveOrUpdateSku(request));
    }

    /**
     * 删除 SKU。
     */
    @Operation(summary = "删除 SKU")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(productSkuService.deleteSku(id));
    }
}
