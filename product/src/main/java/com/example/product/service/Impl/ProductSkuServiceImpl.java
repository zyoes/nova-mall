package com.example.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductSkuListRequest;
import com.example.product.dto.request.ProductSkuRequest;
import com.example.product.dto.response.ProductSkuResponse;
import com.example.product.entity.Product;
import com.example.product.entity.ProductSku;
import com.example.product.mapper.ProductMapper;
import com.example.product.mapper.ProductSkuMapper;
import com.example.product.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品 SKU 服务实现。
 */
@Service
@RequiredArgsConstructor
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {
    private static final int SKU_STATUS_ENABLED = 1;

    private final ProductMapper productMapper;

    /**
     * 保存或更新商品 SKU。
     */
    @Override
    @Transactional
    public boolean saveOrUpdateSku(ProductSkuRequest request) {
        ensureProductExists(request.getProductId());
        ProductSku sku = request.getId() == null
                ? new ProductSku()
                : findSku(request.getId());
        ensureSkuCodeUnique(sku.getId(), request.getSkuCode());

        sku.setProductId(request.getProductId());
        sku.setSkuCode(request.getSkuCode());
        sku.setSpec(request.getSpec());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        sku.setImage(request.getImage());
        sku.setStatus(request.getStatus());

        return this.saveOrUpdate(sku);
    }

    /**
     * 分页查询 SKU 列表。
     */
    @Override
    public PageResponse<ProductSkuResponse> getSkuPage(ProductSkuListRequest request) {
        LambdaQueryWrapper<ProductSku> qw = buildQuery(request);
        Page<ProductSku> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<ProductSkuResponse> list = page.getRecords().stream()
                .map(this::toResponse)
                .toList();

        PageResponse<ProductSkuResponse> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(page.getTotal());
        response.setPage((int) page.getCurrent());
        response.setSize((int) page.getSize());
        return response;
    }

    /**
     * 根据商品ID查询 SKU 列表。
     */
    @Override
    public List<ProductSkuResponse> getSkuListByProductId(Long productId) {
        return this.list(new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getProductId, productId)
                        .eq(ProductSku::getStatus, SKU_STATUS_ENABLED)
                        .orderByDesc(ProductSku::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 删除商品 SKU。
     */
    @Override
    @Transactional
    public boolean deleteSku(Long id) {
        findSku(id);
        return this.removeById(id);
    }

    /**
     * 构建 SKU 查询条件。
     */
    private LambdaQueryWrapper<ProductSku> buildQuery(ProductSkuListRequest request) {
        LambdaQueryWrapper<ProductSku> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            qw.and(wrapper -> wrapper
                    .like(ProductSku::getSkuCode, request.getKeyword())
                    .or()
                    .like(ProductSku::getSpec, request.getKeyword()));
        }
        if (request.getProductId() != null) {
            qw.eq(ProductSku::getProductId, request.getProductId());
        }
        if (request.getStatus() != null) {
            qw.eq(ProductSku::getStatus, request.getStatus());
        }

        qw.orderByDesc(ProductSku::getCreatedAt);
        return qw;
    }

    /**
     * 根据 ID 查询 SKU，不存在时抛出业务异常。
     */
    private ProductSku findSku(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("商品 SKU 不存在"));
    }

    /**
     * 校验商品存在，避免产生没有商品归属的 SKU。
     */
    private void ensureProductExists(Long productId) {
        Long count = productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getId, productId));
        if (count == null || count == 0) {
            throw new CustomValidationException("商品不存在");
        }
    }

    /**
     * 校验 SKU 编码唯一。
     */
    private void ensureSkuCodeUnique(Long currentId, String skuCode) {
        LambdaQueryWrapper<ProductSku> qw = new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getSkuCode, skuCode);
        if (currentId != null) {
            qw.ne(ProductSku::getId, currentId);
        }

        if (this.exists(qw)) {
            throw new CustomValidationException("SKU 编码已存在");
        }
    }

    /**
     * 将 SKU 实体转换为响应对象。
     */
    private ProductSkuResponse toResponse(ProductSku sku) {
        ProductSkuResponse response = new ProductSkuResponse();
        response.setId(sku.getId());
        response.setProductId(sku.getProductId());
        response.setSkuCode(sku.getSkuCode());
        response.setSpec(sku.getSpec());
        response.setPrice(sku.getPrice());
        response.setStock(sku.getStock());
        response.setImage(sku.getImage());
        response.setStatus(sku.getStatus());
        response.setCreatedAt(sku.getCreatedAt());
        return response;
    }
}
