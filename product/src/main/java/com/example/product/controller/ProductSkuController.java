package com.example.product.controller;

import com.example.common.response.R;
import com.example.product.dto.response.ProductSkuResponse;
import com.example.product.service.ProductSkuService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品 SKU 公开接口。
 * 用于用户端查询商品可用 SKU。
 */
@RestController
@RequestMapping("/product/sku")
@RequiredArgsConstructor
public class ProductSkuController {
    private final ProductSkuService productSkuService;

    /**
     * 根据商品ID查询有效 SKU 列表。
     */
    @Operation(summary = "根据商品ID查询有效 SKU 列表")
    @GetMapping("/{productId}")
    public R<List<ProductSkuResponse>> listByProductId(@PathVariable Long productId) {
        return R.ok(productSkuService.getSkuListByProductId(productId));
    }
}
