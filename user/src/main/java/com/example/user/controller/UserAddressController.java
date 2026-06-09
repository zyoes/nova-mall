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

/**
 * 用户收货地址控制器
 * 处理地址的增删改查及设置默认地址等操作
 */
@RestController
@RequestMapping("user-address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 获取用户地址列表（分页）
     *
     * @param request 地址列表请求（包含分页参数和搜索关键词）
     * @return 分页地址列表
     */
    @Operation(summary = "获取用户地址列表")
    @PostMapping("list")
    public R<PageResponse<UserAddressResponse>> getUserAddressList(@Valid @RequestBody UserAddressListRequest request) {
        return R.ok(userAddressService.getUserAddressByUserId(request));
    }

    /**
     * 保存或更新用户地址
     *
     * @param request 用户地址请求（包含收货人信息、地址详情、是否默认）
     * @return 操作结果
     */
    @Operation(summary = "保存或更新用户地址")
    @PostMapping("save-or-update")
    public R<Object> saveOrUpdateUserAddress(@Valid @RequestBody UserAddressRequest request) {
        return R.ok(userAddressService.saveOrUpdateUserAddress(request));
    }

    /**
     * 删除用户地址
     *
     * @param id 用户地址 ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户地址")
    @PostMapping("delete")
    public R<Object> deleteUserAddress(@RequestParam Long id) {
        return R.ok(userAddressService.deleteUserAddress(id));
    }

    /**
     * 设置默认用户地址
     *
     * @param id 用户地址 ID
     * @return 操作结果
     */
    @Operation(summary = "设置默认用户地址")
    @PostMapping("set-default")
    public R<Object> setDefaultUserAddress(@RequestParam Long id) {
        return R.ok(userAddressService.setDefaultUserAddress(id));
    }
}
