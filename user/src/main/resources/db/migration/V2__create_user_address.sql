DROP TABLE IF EXISTS `user_address`;

-- id,userId,name,phone,province,city,district,address,default
CREATE table `user_address`
(
    `id`             bigint NOT NULL COMMENT 'ID',
    `user_id`        bigint NOT NULL COMMENT '用户ID',
    `receiver_name`  varchar(50)  DEFAULT NULL COMMENT '收货人名称',
    `receiver_phone` varchar(20)  DEFAULT NULL COMMENT '收货人电话',
    `province`       varchar(255) DEFAULT NULL COMMENT '省份',
    `city`           varchar(255) DEFAULT NULL COMMENT '城市',
    `district`       varchar(255) DEFAULT NULL COMMENT '区县',
    `address`        varchar(255) DEFAULT NULL COMMENT '地址',
    `is_default`      tinyint(1)   DEFAULT 0 COMMENT '是否默认|【0-非默认，1-默认】',

    -- 通用基础字段
    `created_at`     datetime     DEFAULT NULL COMMENT '创建时间',
    `created_by`     bigint       DEFAULT NULL COMMENT '创建人ID',
    `updated_at`     datetime     DEFAULT NULL COMMENT '更新时间',
    `updated_by`     bigint       DEFAULT NULL COMMENT '修改人ID',
    `deleted`        bigint       DEFAULT 0 COMMENT '逻辑删除标记',
    `deleted_at`     datetime     DEFAULT NULL COMMENT '删除时间',
    `deleted_by`     bigint       DEFAULT NULL COMMENT '删除人ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_receiver_phone` (`receiver_phone`)
) COMMENT ='用户地址';