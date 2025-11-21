package com.agrismart.agrimallbackend.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.service.common.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类。
 *
 * 该类实现了 {@link IProductService} 接口，提供商品相关的业务逻辑实现。
 * 包括商品管理、查询、热销商品推荐等功能。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IProductService
 * @since 1.0
 */
@Service
public class ProductServiceImpl implements IProductService {

    /**
     * 商品数据访问对象。
     */
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> getProductByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> allProduct = productMapper.selectAll();
        PageInfo<Product> pageInfo = new PageInfo<>(allProduct);
        pageInfo.setList(allProduct);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getProductByPageAndContent(Integer pageNum, Integer pageSize, String content) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> selectBySearchContent = productMapper.selectBySearchContent(content);
        PageInfo<Product> pageInfo = new PageInfo<>(selectBySearchContent);
        pageInfo.setList(selectBySearchContent);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> add(Product product) {
        if (product == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(product);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (productMapper.insertSelective(product) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品添加成功！");
    }

    @Override
    public ResponseVo<Boolean> edit(Product product) {
        if (product == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(product);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (productMapper.updateByPrimaryKeySelective(product) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品编辑成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Long id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (productMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "商品删除成功！");
    }

    @Override
    public ResponseVo<PageInfo> getProductByPageAndCategoryIdAndContent(Integer pageNum, Integer pageSize, Long categoryId, String content) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> allProduct = productMapper.selectByCategoryIdAndSearchContent(categoryId, content);
        PageInfo<Product> pageInfo = new PageInfo<>(allProduct);
        pageInfo.setList(allProduct);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public Product selectByPrimaryKey(Long id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Product> selectBySellNumber() {
        return productMapper.selectBySellNumber();
    }

    @Override
    public List<Product> selectBySellNumberLimit(int limit) {
        int effectiveLimit = limit <= 0 ? 5 : limit;
        return productMapper.selectBySellNumberLimit(effectiveLimit);
    }
}

