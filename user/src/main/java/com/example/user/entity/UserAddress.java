package com.example.user.entity;

import com.example.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAddress extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人名称
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 地址
     */
    private String address;

    /**
     * 是否默认|【0-非默认，1-默认】
     */
    private Integer isDefault;

}