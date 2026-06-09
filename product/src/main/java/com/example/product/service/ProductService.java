package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductListRequest;
import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Product;

/**
 * 商品服务
 */
public interface ProductService extends IService<Product> {
    /**
     * 保存或更新商品
     */
    boolean saveOrUpdateProduct(ProductRequest request);

    /**
     * 分页查询商品列表
     */
    PageResponse<ProductResponse> getProductPage(ProductListRequest request);

    /**
     * 分页查询管理端商品列表
     */
    PageResponse<ProductResponse> getAdminProductPage(ProductListRequest request);

    /**
     * 查询商品详情
     */
    ProductResponse getProductDetail(Long id);

    /**
     * 根据id删除商品
     */
    boolean deleteProduct(Long id);
}
