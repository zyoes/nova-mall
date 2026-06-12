DROP TABLE IF EXISTS `product_sku`;

CREATE TABLE `product_sku`
(
    `id`         bigint         NOT NULL COMMENT 'ID',
    `product_id` bigint         NOT NULL COMMENT '所属商品ID',
    `sku_code`   varchar(100)   NOT NULL COMMENT 'SKU编码',
    `spec`       varchar(255)   DEFAULT NULL COMMENT '规格描述，如"红色/128G"',
    `price`      decimal(10,2)  NOT NULL COMMENT '价格（元）',
    `stock`      int            DEFAULT 0 COMMENT '库存数量',
    `image`      varchar(512)   DEFAULT NULL COMMENT 'SKU图片URL',
    `status`     tinyint        DEFAULT 1 COMMENT 'SKU状态｜【1-有效、0-无效】',

    `created_at` datetime       DEFAULT NULL COMMENT '创建时间',
    `created_by` bigint         DEFAULT NULL COMMENT '创建人ID',
    `updated_at` datetime       DEFAULT NULL COMMENT '更新时间',
    `updated_by` bigint         DEFAULT NULL COMMENT '修改人ID',
    `deleted`    bigint         DEFAULT 0 COMMENT '逻辑删除标记',
    `deleted_at` datetime       DEFAULT NULL COMMENT '删除时间',
    `deleted_by` bigint         DEFAULT NULL COMMENT '删除人ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    INDEX `idx_product_id` (`product_id`)
) COMMENT ='商品SKU';