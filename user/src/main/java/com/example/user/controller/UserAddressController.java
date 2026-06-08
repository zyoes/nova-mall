package com.example.user.controller;

import com.example.common.response.PageResponse;
import com.example.common.response.R;
import com.example.user.dto.request.UserAddressListRequest;
import com.example.user.dto.request.UserAddressRequest;
import com.example.user.dto.response.UserAddressResponse;
import com.example.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user-address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 获取用户地址列表
     */
    @Operation(summary = "获取用户地址列表")
    @PostMapping("list")
    public R<PageResponse<UserAddressResponse>> getUserAddressList(@Valid @RequestBody UserAddressListRequest request) {
        return R.ok(userAddressService.getUserAddressByUserId(request));
    }

    /**
     * 保存或更新用户地址
     */
    @Operation(summary = "保存或更新用户地址")
    @PostMapping("save-or-update")
    public R<Object> saveOrUpdateUserAddress(@Valid @RequestBody UserAddressRequest request) {
        return R.ok(userAddressService.saveOrUpdateUserAddress(request));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @PostMapping("delete")
    public R<Object> deleteUserAddress(Long id) {
        return R.ok(userAddressService.deleteUserAddress(id));
    }

    /**
     * 设置默认用户地址
     */
    @Operation(summary = "设置默认用户地址")
    @PostMapping("set-default")
    public R<Object> setDefaultUserAddress(Long id) {
        return R.ok(userAddressService.setDefaultUserAddress(id));
    }
}
