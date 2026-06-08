package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.response.PageResponse;
import com.example.user.dto.request.UserAddressListRequest;
import com.example.user.dto.request.UserAddressRequest;
import com.example.user.dto.response.UserAddressResponse;
import com.example.user.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    /**
     * 根据用户 ID 获取用户地址
     *
     * @param request 用户地址列表请求
     * @return 用户地址
     */
    PageResponse<UserAddressResponse> getUserAddressByUserId(UserAddressListRequest request);

    /**
     * 设置用户地址
     *
     * @param request 用户地址请求
     * @return 是否成功
     */
    boolean saveOrUpdateUserAddress(UserAddressRequest request);

    /**
     * 删除用户地址
     *
     * @param id 用户地址ID
     * @return 是否成功
     */
    boolean deleteUserAddress(Long id);

    /**
     * 设置默认用户地址
     *
     * @param id 用户地址ID
     * @return 是否成功
     */
    boolean setDefaultUserAddress(Long id);


}
