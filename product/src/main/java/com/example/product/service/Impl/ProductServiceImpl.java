package com.example.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductListRequest;
import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.request.ProductSkuRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Product;
import com.example.product.entity.ProductCategory;
import com.example.product.entity.ProductSku;
import com.example.product.mapper.ProductCategoryMapper;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;
import com.example.product.service.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务实现。
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    private static final int PRODUCT_STATUS_ON_SALE = 2;

    private final ProductSkuService productSkuService;
    private final ProductCategoryMapper productCategoryMapper;

    /**
     * 保存或更新商品。
     */
    @Override
    @Transactional
    public boolean saveOrUpdateProduct(ProductRequest request) {
        ensureCategoryExists(request.getCategoryId());
        ensureSkuListNotEmpty(request.getSkuList());
        Product product = request.getId() == null ? new Product() : findProduct(request.getId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setCoverImage(request.getCoverImage());
        product.setDetailImages(request.getDetailImages());
        product.setStatus(request.getStatus());
        product.setSort(request.getSort());

        boolean saved = this.saveOrUpdate(product);
        if (saved) {
            replaceProductSkus(product.getId(), request.getSkuList());
        }

        return saved;
    }

    /**
     * 分页查询商品列表。
     */
    @Override
    public PageResponse<ProductResponse> getProductPage(ProductListRequest request) {
        LambdaQueryWrapper<Product> qw = buildQuery(request, false);
        qw.eq(Product::getStatus, PRODUCT_STATUS_ON_SALE);
        return executePageQuery(request, qw);
    }

    /**
     * 分页查询管理端商品列表。
     */
    @Override
    public PageResponse<ProductResponse> getAdminProductPage(ProductListRequest request) {
        return executePageQuery(request, buildQuery(request, true));
    }

    /**
     * 查询商品详情。
     */
    @Override
    public ProductResponse getProductDetail(Long id) {
        Product product = this.getOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getId, id)
                .eq(Product::getStatus, PRODUCT_STATUS_ON_SALE));
        if (product == null) {
            throw new CustomValidationException("商品不存在或已下架");
        }

        ProductResponse response = toResponse(product);
        response.setCategoryName(getCategoryName(product.getCategoryId()));
        response.setSkuList(productSkuService.getSkuListByProductId(product.getId()));
        return response;
    }

    /**
     * 删除商品。
     */
    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        findProduct(id);
        productSkuService.remove(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, id));
        return this.removeById(id);
    }

    /**
     * 构建商品查询条件。
     */
    private LambdaQueryWrapper<Product> buildQuery(ProductListRequest request, boolean includeStatus) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            qw.like(Product::getName, request.getKeyword());
        }
        if (request.getCategoryId() != null) {
            qw.eq(Product::getCategoryId, request.getCategoryId());
        }
        if (includeStatus && request.getStatus() != null) {
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
        Map<Long, String> categoryNameMap = getCategoryNameMap(page.getRecords());
        List<ProductResponse> list = page.getRecords().stream()
                .map(product -> toResponse(product, categoryNameMap))
                .toList();

        PageResponse<ProductResponse> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(page.getTotal());
        response.setPage((int) page.getCurrent());
        response.setSize((int) page.getSize());
        return response;
    }

    /**
     * 批量查询商品分类名称，避免商品列表逐条查询分类。
     */
    private Map<Long, String> getCategoryNameMap(List<Product> products) {
        Set<Long> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Map.of();
        }

        return productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                        .in(ProductCategory::getId, categoryIds))
                .stream()
                .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getName));
    }

    /**
     * 查询单个分类名称。
     */
    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        ProductCategory category = productCategoryMapper.selectById(categoryId);
        return category == null ? null : category.getName();
    }

    /**
     * 根据 ID 查询商品，不存在时抛出业务异常。
     */
    private Product findProduct(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("商品不存在"));
    }

    /**
     * 校验商品分类存在，避免商品绑定不存在的分类。
     */
    private void ensureCategoryExists(Long categoryId) {
        Long count = productCategoryMapper.selectCount(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getId, categoryId));
        if (count == null || count == 0) {
            throw new CustomValidationException("商品分类不存在");
        }
    }

    /**
     * 校验商品至少包含一个 SKU。
     */
    private void ensureSkuListNotEmpty(List<ProductSkuRequest> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            throw new CustomValidationException("商品至少需要一个 SKU");
        }
    }

    /**
     * 全量替换商品 SKU：先删除旧 SKU，再插入本次提交的 SKU。
     */
    private void replaceProductSkus(Long productId, List<ProductSkuRequest> skuList) {
        // TODO: 如果后续订单关联了 SKU，不应使用物理/逻辑删除，应该修理原有SKU/停用不用的SKU/保留历史 SKU，只不再对用户端展示
        productSkuService.remove(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, productId));

        for (ProductSkuRequest skuRequest : skuList) {
            skuRequest.setId(null);
            skuRequest.setProductId(productId);
            productSkuService.saveOrUpdateSku(skuRequest);
        }
    }

    /**
     * 将商品实体转换为响应对象。
     */
    private ProductResponse toResponse(Product product) {
        return toResponse(product, Map.of());
    }

    /**
     * 将商品实体转换为响应对象。
     */
    private ProductResponse toResponse(Product product, Map<Long, String> categoryNameMap) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategoryId());
        response.setCategoryName(categoryNameMap.get(product.getCategoryId()));
        response.setCoverImage(product.getCoverImage());
        response.setDetailImages(product.getDetailImages());
        response.setStatus(product.getStatus());
        response.setSort(product.getSort());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
