package com.agrismart.agrimallbackend.service.home.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Collect;
import com.agrismart.agrimallbackend.mapper.home.CollectMapper;
import com.agrismart.agrimallbackend.service.home.ICollectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品收藏服务实现类。
 *
 * 该类实现了 {@link ICollectService} 接口，提供商品收藏相关的业务逻辑实现。
 * 收藏数据用于个性化推荐算法。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICollectService
 * @since 1.0
 */
@Service
public class CollectServiceImpl implements ICollectService {

    /**
     * 收藏数据访问对象。
     */
    @Autowired
    private CollectMapper collectMapper;

    @Override
    public List<Collect> findCollectByUserId(Long userId) {
        return collectMapper.findCollectByUserId(userId);
    }

    @Override
    public ResponseVo<Boolean> add(Long id, HttpServletRequest request) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        Collect collectByUserIdAndProductId = collectMapper.findCollectByUserIdAndProductId(uid, id);
        if (collectByUserIdAndProductId != null) {
            return ResponseVo.errorByMsg(CodeMsg.COLLECT_ALREADY_EXIST);
        }
        Collect collect = new Collect(uid, id);
        if (collectMapper.insertSelective(collect) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.COLLECT_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "添加收藏成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Long id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (collectMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.COLLECT_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除收藏成功！");
    }

    @Override
    public ResponseVo<PageInfo> getCollectByPageAndUserId(Integer pageNum, Integer pageSize, Long userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> allCollect = collectMapper.findCollectByUserId(userId);
        PageInfo<Collect> pageInfo = new PageInfo<>(allCollect);
        pageInfo.setList(allCollect);
        return ResponseVo.success(pageInfo);
    }
}

