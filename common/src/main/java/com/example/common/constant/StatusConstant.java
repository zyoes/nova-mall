package com.example.common.constant;

/**
 * 业务状态常量。
 */
public final class StatusConstant {
    private StatusConstant() {
    }

    /**
     * 商品状态：草稿。
     */
    public static final int PRODUCT_STATUS_DRAFT = 1;

    /**
     * 商品状态：上架。
     */
    public static final int PRODUCT_STATUS_ON_SALE = 2;

    /**
     * 商品状态：下架。
     */
    public static final int PRODUCT_STATUS_OFF_SALE = 3;

    /**
     * SKU 状态：无效。
     */
    public static final int SKU_STATUS_DISABLED = 0;

    /**
     * SKU 状态：有效。
     */
    public static final int SKU_STATUS_ENABLED = 1;
}
