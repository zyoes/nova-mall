package com.example.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.PageResponse;
import com.example.product.dto.request.ProductCategoryListRequest;
import com.example.product.dto.request.ProductCategoryRequest;
import com.example.product.dto.response.ProductCategoryResponse;
import com.example.product.entity.Product;
import com.example.product.entity.ProductCategory;
import com.example.product.mapper.ProductCategoryMapper;
import com.example.product.service.ProductCategoryService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    private final ProductService productService;

    /**
     * 保存或更新商品分类
     */
    @Override
    @Transactional
    public boolean saveOrUpdateCategory(ProductCategoryRequest request) {
        ProductCategory category = request.getId() == null ? new ProductCategory() : findCategory(request.getId());
        Long parentId = request.getParentId() == null ? 0L : request.getParentId();
        Integer level = resolveCategoryLevel(category.getId(), parentId);
        ensureCategoryNameUnique(category.getId(), parentId, request.getName());

        category.setName(request.getName());
        category.setParentId(parentId);
        category.setLevel(level);
        category.setSort(request.getSort());
        category.setIcon(request.getIcon());

        return this.saveOrUpdate(category);
    }

    /**
     * 分页查询商品分类列表
     */
    @Override
    public PageResponse<ProductCategoryResponse> getCategoryPage(ProductCategoryListRequest request) {
        LambdaQueryWrapper<ProductCategory> qw = buildQuery(request);
        Page<ProductCategory> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<ProductCategoryResponse> list = page.getRecords().stream()
                .map(this::toResponse)
                .toList();

        PageResponse<ProductCategoryResponse> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(page.getTotal());
        response.setPage((int) page.getCurrent());
        response.setSize((int) page.getSize());
        return response;
    }

    /**
     * 查询商品分类树
     */
    @Override
    public List<ProductCategoryResponse> getCategoryTree() {
        List<ProductCategory> categories = this.list(new LambdaQueryWrapper<ProductCategory>()
                .orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreatedAt));

        Map<Long, ProductCategoryResponse> nodeMap = categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toMap(ProductCategoryResponse::getId, Function.identity()));

        List<ProductCategoryResponse> roots = new ArrayList<>();
        for (ProductCategoryResponse node : nodeMap.values()) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
            } else {
                nodeMap.get(parentId).getChildren().add(node);
            }
        }

        sortTree(roots);
        return roots;
    }

    /**
     * 删除商品分类
     */
    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        findCategory(id);
        if (this.exists(new LambdaQueryWrapper<ProductCategory>().eq(ProductCategory::getParentId, id))) {
            throw new CustomValidationException("当前分类下存在子分类，不能删除");
        }
        if (productService.exists(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id))) {
            throw new CustomValidationException("当前分类已绑定商品，不能删除");
        }
        return this.removeById(id);
    }

    /**
     * 构建分类查询条件
     */
    private LambdaQueryWrapper<ProductCategory> buildQuery(ProductCategoryListRequest request) {
        LambdaQueryWrapper<ProductCategory> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            qw.like(ProductCategory::getName, request.getKeyword());
        }
        if (request.getParentId() != null) {
            qw.eq(ProductCategory::getParentId, request.getParentId());
        }
        if (request.getLevel() != null) {
            qw.eq(ProductCategory::getLevel, request.getLevel());
        }

        qw.orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreatedAt);
        return qw;
    }

    /**
     * 根据 ID 查询分类，不存在时抛出业务异常
     */
    private ProductCategory findCategory(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("商品分类不存在"));
    }

    /**
     * 根据父分类计算当前分类层级，并校验父子关系合法性
     * @param currentId 当前分类 ID
     * @param parentId 父分类 ID
     * @return 当前分类层级
     */
    private Integer resolveCategoryLevel(Long currentId, Long parentId) {
        if (parentId == null || parentId == 0L) {
            return 1;
        }
        if (currentId != null && currentId.equals(parentId)) {
            throw new CustomValidationException("父分类不能选择当前分类本身");
        }

        ProductCategory parent = findCategory(parentId);
        ensureNoCircularParent(currentId, parent);
        return parent.getLevel() + 1;
    }

    /**
     * 防止形成 A -> B -> A 这类循环父子关系
     */
    private void ensureNoCircularParent(Long currentId, ProductCategory parent) {
        if (currentId == null) {
            return;
        }

        ProductCategory cursor = parent;
        while (cursor != null) {
            if (currentId.equals(cursor.getId())) {
                throw new CustomValidationException("不能将分类移动到自己的子分类下");
            }
            if (cursor.getParentId() == null || cursor.getParentId() == 0L) {
                break;
            }
            cursor = this.getById(cursor.getParentId());
        }
    }

    /**
     * 校验同一父分类下分类名称不重复。
     */
    private void ensureCategoryNameUnique(Long currentId, Long parentId, String name) {
        LambdaQueryWrapper<ProductCategory> qw = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getParentId, parentId)
                .eq(ProductCategory::getName, name);
        if (currentId != null) {
            qw.ne(ProductCategory::getId, currentId);
        }

        if (this.exists(qw)) {
            throw new CustomValidationException("同一父分类下分类名称不能重复");
        }
    }

    /**
     * 将分类实体转换为响应对象
     */
    private ProductCategoryResponse toResponse(ProductCategory category) {
        ProductCategoryResponse response = new ProductCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());
        response.setLevel(category.getLevel());
        response.setSort(category.getSort());
        response.setIcon(category.getIcon());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }

    /**
     * 按 sort 递归排序分类树
     */
    private void sortTree(List<ProductCategoryResponse> nodes) {
        nodes.sort(Comparator.comparing(ProductCategoryResponse::getSort,
                Comparator.nullsLast(Integer::compareTo)));
        for (ProductCategoryResponse node : nodes) {
            sortTree(node.getChildren());
        }
    }
}
