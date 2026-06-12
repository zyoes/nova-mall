package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductCategoryListRequest;
import com.example.product.dto.request.ProductCategoryRequest;
import com.example.product.dto.response.ProductCategoryResponse;
import com.example.product.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类服务
 */
public interface ProductCategoryService extends IService<ProductCategory> {
    /**
     * 保存或更新商品分类
     */
    boolean saveOrUpdateCategory(ProductCategoryRequest request);

    /**
     * 分页查询商品分类列表
     */
    PageResponse<ProductCategoryResponse> getCategoryPage(ProductCategoryListRequest request);

    /**
     * 查询商品分类树
     */
    List<ProductCategoryResponse> getCategoryTree();

    /**
     * 根据id删除商品分类
     */
    boolean deleteCategory(Long id);
}
