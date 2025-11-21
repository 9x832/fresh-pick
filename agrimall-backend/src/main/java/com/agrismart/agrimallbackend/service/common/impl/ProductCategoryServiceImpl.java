package com.agrismart.agrimallbackend.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.ProductCategory;
import com.agrismart.agrimallbackend.mapper.common.ProductCategoryMapper;
import com.agrismart.agrimallbackend.service.common.IProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务实现类。
 *
 * 该类实现了 {@link IProductCategoryService} 接口，提供商品分类相关的业务逻辑实现。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IProductCategoryService
 * @since 1.0
 */
@Service
public class ProductCategoryServiceImpl implements IProductCategoryService {

    /**
     * 商品分类数据访问对象。
     */
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public ResponseVo<PageInfo> getProductCategoryByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductCategory> allProductCategory = productCategoryMapper.selectAll();
        PageInfo<ProductCategory> pageInfo = new PageInfo<>(allProductCategory);
        pageInfo.setList(allProductCategory);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getProductCategoryByPageAndContent(Integer pageNum, Integer pageSize, String content) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductCategory> selectBySearchContent = productCategoryMapper.selectBySearchContent(content);
        PageInfo<ProductCategory> pageInfo = new PageInfo<>(selectBySearchContent);
        pageInfo.setList(selectBySearchContent);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> add(ProductCategory productCategory) {
        if (productCategory == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(productCategory);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (productCategoryMapper.insertSelective(productCategory) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_CATEGORY_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品种类添加成功！");
    }

    @Override
    public ResponseVo<Boolean> edit(ProductCategory productCategory) {
        if (productCategory == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(productCategory);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (productCategoryMapper.updateByPrimaryKeySelective(productCategory) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_CATEGORY_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品种类修改成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Long id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (productCategoryMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_CATEGORY_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品种类删除成功！");
    }
}

