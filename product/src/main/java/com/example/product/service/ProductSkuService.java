package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductSkuListRequest;
import com.example.product.dto.request.ProductSkuRequest;
import com.example.product.dto.response.ProductSkuResponse;
import com.example.product.entity.ProductSku;

import java.util.List;

/**
 * 商品 SKU 服务
 */
public interface ProductSkuService extends IService<ProductSku> {
    /**
     * 保存或更新商品 SKU
     */
    boolean saveOrUpdateSku(ProductSkuRequest request);

    /**
     * 分页查询 SKU 列表
     */
    PageResponse<ProductSkuResponse> getSkuPage(ProductSkuListRequest request);

    /**
     * 根据商品ID查询 SKU 列表
     */
    List<ProductSkuResponse> getSkuListByProductId(Long productId);

    /**
     * 删除商品 SKU
     */
    boolean deleteSku(Long id);
}
