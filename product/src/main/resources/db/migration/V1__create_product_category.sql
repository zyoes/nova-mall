DROP TABLE IF EXISTS `product_category`;

CREATE TABLE `product_category`
(
    `id`         bigint       NOT NULL COMMENT 'ID',
    `name`       varchar(100) NOT NULL COMMENT '分类名称',
    `parent_id`  bigint       DEFAULT 0 COMMENT '父分类ID｜【0-顶级分类】',
    `level`      tinyint      DEFAULT 1 COMMENT '层级｜【1-一级、2-二级、3-三级】',
    `sort`       int          DEFAULT 0 COMMENT '排序值，越小越靠前',
    `icon`       varchar(255) DEFAULT NULL COMMENT '图标URL',

    `created_at` datetime     DEFAULT NULL COMMENT '创建时间',
    `created_by` bigint       DEFAULT NULL COMMENT '创建人ID',
    `updated_at` datetime     DEFAULT NULL COMMENT '更新时间',
    `updated_by` bigint       DEFAULT NULL COMMENT '修改人ID',
    `deleted`    bigint       DEFAULT 0 COMMENT '逻辑删除标记',
    `deleted_at` datetime     DEFAULT NULL COMMENT '删除时间',
    `deleted_by` bigint       DEFAULT NULL COMMENT '删除人ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_parent_id` (`parent_id`)
) COMMENT ='商品分类';