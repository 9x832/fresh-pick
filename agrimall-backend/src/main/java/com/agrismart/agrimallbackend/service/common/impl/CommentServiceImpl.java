package com.agrismart.agrimallbackend.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Comment;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.mapper.common.CommentMapper;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类。
 *
 * 该类实现了 {@link ICommentService} 接口，提供商品评论相关的业务逻辑实现。
 * 包括评论提交、查询、删除等功能。提交评论时会自动更新商品的评论数量。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @since 1.0
 */
@Service
public class CommentServiceImpl implements ICommentService {

    /**
     * 评论数据访问对象。
     */
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 商品数据访问对象。
     * 用于更新商品的评论数量。
     */
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public ResponseVo<Boolean> submitComment(Long uid, Comment comment) {
        comment.setUserId(uid);
        CodeMsg validate = ValidateEntityUtil.validate(comment);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (commentMapper.insertSelective(comment) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.COMMENT_ADD_ERROR);
        }
        Product product = productMapper.selectByPrimaryKey(comment.getProductId());
        product.setCommentNum(product.getCommentNum() + 1);
        if (productMapper.updateByPrimaryKeySelective(product) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.COMMENT_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "评论发表成功！");
    }

    @Override
    public ResponseVo<PageInfo> selectByProductIdAndPage(Long productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> commentList = commentMapper.selectByProductId(productId);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        pageInfo.setList(commentList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> selectByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> commentList = commentMapper.selectAll();
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        pageInfo.setList(commentList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> selectByPageAndSearchContent(String content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> commentList = commentMapper.selectBySearchContent(content);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        pageInfo.setList(commentList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> deleteComment(Long commentId) {
        if (commentId == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            return ResponseVo.errorByMsg(CodeMsg.COMMENT_NOT_EXIST);
        }
        if (commentMapper.deleteByPrimaryKey(commentId) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.COMMENT_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "成功删除评论！");
    }

    @Override
    public ResponseVo<PageInfo> selectByPageAndUserId(Integer pageNum, Integer pageSize, Long userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> commentList = commentMapper.selectByUserId(userId);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        pageInfo.setList(commentList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public List<Comment> selectByProductId(Long productId) {
        return commentMapper.selectByProductId(productId);
    }
}

