DROP TABLE IF EXISTS `product`;

CREATE TABLE `product`
(
    `id`            bigint        NOT NULL COMMENT 'ID',
    `name`          varchar(200)  NOT NULL COMMENT '商品名称',
    `description`   text          DEFAULT NULL COMMENT '商品描述',
    `category_id`   bigint        NOT NULL COMMENT '所属分类ID',
    `cover_image`   varchar(512)  DEFAULT NULL COMMENT '封面图URL',
    `detail_images` text          DEFAULT NULL COMMENT '详情图URL列表（JSON数组）',
    `status`        tinyint       DEFAULT 1 COMMENT '商品状态｜【1-草稿、2-上架、3-下架】',
    `sort`          int           DEFAULT 0 COMMENT '排序值，越小越靠前',

    `created_at`    datetime      DEFAULT NULL COMMENT '创建时间',
    `created_by`    bigint        DEFAULT NULL COMMENT '创建人ID',
    `updated_at`    datetime      DEFAULT NULL COMMENT '更新时间',
    `updated_by`    bigint        DEFAULT NULL COMMENT '修改人ID',
    `deleted`       bigint        DEFAULT 0 COMMENT '逻辑删除标记',
    `deleted_at`    datetime      DEFAULT NULL COMMENT '删除时间',
    `deleted_by`    bigint        DEFAULT NULL COMMENT '删除人ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_category_id` (`category_id`)
) COMMENT ='商品/SPU';