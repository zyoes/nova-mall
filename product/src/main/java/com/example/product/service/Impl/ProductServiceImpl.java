package com.example.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductListRequest;
import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品服务实现。
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    /**
     * 保存或更新商品。
     */
    @Override
    @Transactional
    public boolean saveOrUpdateProduct(ProductRequest request) {
        Product product = request.getId() == null ? new Product() : findProduct(request.getId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setCoverImage(request.getCoverImage());
        product.setDetailImages(request.getDetailImages());
        product.setStatus(request.getStatus());
        product.setSort(request.getSort());

        return this.saveOrUpdate(product);
    }

    /**
     * 分页查询商品列表。
     */
    @Override
    public PageResponse<ProductResponse> getProductPage(ProductListRequest request) {
        return executePageQuery(request, buildQuery(request));
    }

    /**
     * 分页查询管理端商品列表。
     */
    @Override
    public PageResponse<ProductResponse> getAdminProductPage(ProductListRequest request) {
        return executePageQuery(request, buildQuery(request));
    }

    /**
     * 查询商品详情。
     */
    @Override
    public ProductResponse getProductDetail(Long id) {
        return toResponse(findProduct(id));
    }

    /**
     * 删除商品。
     */
    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        findProduct(id);
        return this.removeById(id);
    }

    /**
     * 构建商品查询条件。
     */
    private LambdaQueryWrapper<Product> buildQuery(ProductListRequest request) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            qw.like(Product::getName, request.getKeyword());
        }
        if (request.getCategoryId() != null) {
            qw.eq(Product::getCategoryId, request.getCategoryId());
        }
        if (request.getStatus() != null) {
            qw.eq(Product::getStatus, request.getStatus());
        }

        qw.orderByAsc(Product::getSort)
                .orderByDesc(Product::getCreatedAt);
        return qw;
    }

    /**
     * 执行商品分页查询。
     */
    private PageResponse<ProductResponse> executePageQuery(ProductListRequest request,
                                                           LambdaQueryWrapper<Product> qw) {
        Page<Product> page = this.page(Page.of(request.getPage(), request.getSize()), qw);
        List<ProductResponse> list = page.getRecords().stream()
                .map(this::toResponse)
                .toList();

        PageResponse<ProductResponse> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(page.getTotal());
        response.setPage((int) page.getCurrent());
        response.setSize((int) page.getSize());
        return response;
    }

    /**
     * 根据 ID 查询商品，不存在时抛出业务异常。
     */
    private Product findProduct(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("商品不存在"));
    }

    /**
     * 将商品实体转换为响应对象。
     */
    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategoryId());
        response.setCoverImage(product.getCoverImage());
        response.setDetailImages(product.getDetailImages());
        response.setStatus(product.getStatus());
        response.setSort(product.getSort());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
