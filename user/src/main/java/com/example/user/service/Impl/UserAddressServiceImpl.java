package com.example.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.PageResponse;
import com.example.user.dto.request.UserAddressListRequest;
import com.example.user.dto.request.UserAddressRequest;
import com.example.user.dto.response.UserAddressResponse;
import com.example.user.entity.UserAddress;
import com.example.user.mapper.UserAddressMapper;
import com.example.user.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户收货地址服务实现类
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    /**
     * 获取当前登录用户的地址列表（分页）
     * 支持关键词搜索（收货人、手机号、省市区、详细地址），默认地址优先排序
     *
     * @param request 地址列表请求（包含分页参数和搜索关键词）
     * @return 分页地址列表
     */
    @Override
    public PageResponse<UserAddressResponse> getUserAddressByUserId(UserAddressListRequest request) {
        // 分页
        Page<UserAddress> page = Page.of(request.getPage(), request.getSize());

        // 获取当前登录用户 ID
        Long userId = UserContext.get();

        // 查询
        QueryWrapper<UserAddress> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);

        // 关键词
        if (StringUtils.hasText(request.getKeyword())) {
            qw.and(qw1 -> qw1
                    .or().like("receiver_name", request.getKeyword())
                    .or().like("receiver_phone", request.getKeyword())
                    .or().like("province", request.getKeyword())
                    .or().like("city", request.getKeyword())
                    .or().like("district", request.getKeyword())
                    .or().like("address", request.getKeyword()));
        }
        qw.orderByDesc("is_default").orderByDesc("updated_at");

        this.page(page, qw);

        List<UserAddress> userAddressList = page.getRecords();
        List<UserAddressResponse> userAddressResponse = BeanUtil.copyToList(userAddressList, UserAddressResponse.class);

        PageResponse<UserAddressResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(userAddressResponse);

        return response;
    }

    /**
     * 保存或更新用户地址
     * 新增时自动设置 userId；更新时校验地址归属权
     * 若设为默认地址，会先取消该用户其他默认地址
     *
     * @param request 用户地址请求
     * @return 是否操作成功
     * @throws CustomValidationException 地址重复或无权限时抛出
     */
    @Override
    @Transactional
    public boolean saveOrUpdateUserAddress(UserAddressRequest request) {
        Long id = request.getId();

        // 验证当前用户是否已存在相同收货地址，不包括当前修改的地址
        QueryWrapper<UserAddress> qw = new QueryWrapper<>();
        qw.eq("user_id", UserContext.get());
        qw.eq("receiver_name", request.getReceiverName());
        qw.eq("receiver_phone", request.getReceiverPhone());
        qw.eq("province", request.getProvince());
        qw.eq("city", request.getCity());
        qw.eq("district", request.getDistrict());
        qw.eq("address", request.getAddress());

        if (id != null) {
            qw.ne("id", id);
        }
        if (this.exists(qw)) {
            throw new CustomValidationException("当前用户已存在相同收货地址");
        }

        // 设置默认地址
        if (request.getIsDefault().equals(1)) {
            // 取消当前用户默认地址
            cancelCurrentUserDefaultAddress(id);
        }

        // 新增
        if (id == null) {
            UserAddress userAddress = BeanUtil.copyProperties(request, UserAddress.class);
            userAddress.setUserId(UserContext.get());

            return this.save(userAddress);
        } else {
            UserAddress userAddress = this.findUserAddressById(request.getId());

            // 验证当前用户是否是地址的所属用户
            if (!userAddress.getUserId().equals(UserContext.get())) {
                throw new CustomValidationException("当前用户无权限修改该地址");
            }

            BeanUtil.copyProperties(request, userAddress, "id","userId");
            return this.updateById(userAddress);
        }
    }

    /**
     * 删除用户地址
     * 先校验地址存在性，再校验归属权，最后执行逻辑删除
     *
     * @param id 用户地址 ID
     * @return 是否删除成功
     * @throws CustomValidationException 地址不存在或无权限时抛出
     */
    @Override
    @Transactional
    public boolean deleteUserAddress(Long id) {
        // 验证用户地址存在
        UserAddress userAddress = findUserAddressById(id);

        // 验证当前用户是否是地址的所属用户
        if (!userAddress.getUserId().equals(UserContext.get())) {
            throw new CustomValidationException("当前用户无权限删除该地址");
        }

        return this.removeById(id);
    }

    /**
     * 设置默认用户地址
     * 先校验地址存在性和归属权，再取消该用户其他默认地址，最后将目标地址设为默认
     *
     * @param id 用户地址 ID
     * @return 是否设置成功
     * @throws CustomValidationException 地址不存在或无权限时抛出
     */
    @Override
    @Transactional
    public boolean setDefaultUserAddress(Long id) {
        // 设置默认地址
        UserAddress userAddress = findUserAddressById(id);

        // 验证当前用户是否是地址的所属用户
        if (!userAddress.getUserId().equals(UserContext.get())) {
            throw new CustomValidationException("当前用户无权限设置默认地址");
        }

        // 取消当前用户默认地址
        cancelCurrentUserDefaultAddress(id);

        if (userAddress.getIsDefault().equals(0)) {
            userAddress.setIsDefault(1);
        }

        return this.updateById(userAddress);
    }

    // ==================== 逻辑抽取辅助方法 ====================

    /**
     * 根据 ID 查询用户地址
     *
     * @param id 用户地址 ID
     * @return 用户地址
     */
    private UserAddress findUserAddressById(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("用户地址不存在"));
    }

    /**
     * 取消当前用户默认地址
     *
     */
    private void cancelCurrentUserDefaultAddress(Long id) {
        QueryWrapper<UserAddress> qw = new QueryWrapper<>();
        qw.eq("user_id", UserContext.get());
        qw.eq("is_default", 1);
        if(id != null) {
            qw.ne("id", id);
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setIsDefault(0);

        this.update(userAddress, qw);
    }
}
