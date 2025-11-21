package com.agrismart.agrimallbackend.service.home.impl;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Address;
import com.agrismart.agrimallbackend.mapper.home.AddressMapper;
import com.agrismart.agrimallbackend.service.home.IAddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址服务实现类。
 *
 * 该类实现了 {@link IAddressService} 接口，提供收货地址相关的业务逻辑实现。
 * 包括地址管理、首选地址设置等功能，每个用户最多可以添加 3 个地址。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.IAddressService
 * @since 1.0
 */
@Service
public class AddressServiceImpl implements IAddressService {

    /**
     * 地址数据访问对象。
     */
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ResponseVo<Boolean> add(Address address, HttpServletRequest request) {
        if (address == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        address.setUserId(uid);
        CodeMsg validate = ValidateEntityUtil.validate(address);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        List<Address> addressByUserId = addressMapper.findAddressByUserId(uid);
        if (addressByUserId.size() >= 3) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_NUM_EXCEED_LIMIT);
        }
        if (addressMapper.insertSelective(address) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "添加地址成功！");
    }

    @Override
    public ResponseVo<Boolean> update(Address address, HttpServletRequest request) {
        if (address == null || address.getId() == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        Address existed = addressMapper.selectByPrimaryKey(address.getId());
        if (existed == null) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_NOT_EXIST);
        }
        if (!uid.equals(existed.getUserId())) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_NO_AUTHORITY);
        }
        address.setUserId(uid);
        if (address.getFirstSelected() == null) {
            address.setFirstSelected(existed.getFirstSelected());
        }
        CodeMsg validate = ValidateEntityUtil.validate(address);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (addressMapper.updateByPrimaryKeySelective(address) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "更新地址成功！");
    }

    @Override
    public List<Address> findAddressByUserId(Long userId) {
        return addressMapper.findAddressByUserId(userId);
    }

    @Override
    public ResponseVo<Boolean> setFirstSelected(Long id, HttpServletRequest request) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        if (addressMapper.updateFirstSelectedByUserId(uid, AddressFirstSelectedEnum.NO.getCode()) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_SET_FIRST_SELECTED_ERROR);
        }
        Address address = addressMapper.selectByPrimaryKey(id);
        address.setFirstSelected(AddressFirstSelectedEnum.YES.getCode());
        if (addressMapper.updateByPrimaryKeySelective(address) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_SET_FIRST_SELECTED_ERROR);
        }
        return ResponseVo.successByMsg(true, "成功设置该地址为订单首选！");
    }

    @Override
    public ResponseVo<Boolean> delete(Long id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (addressMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADDRESS_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除地址成功！");
    }

    @Override
    public Address selectByUserIdAndFirstSelected(Long userId, Integer firstSelected) {
        return addressMapper.selectByUserIdAndFirstSelected(userId, firstSelected);
    }
}

