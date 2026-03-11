package com.github.dingdaoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.mapper.PushConfigMapper;
import com.github.dingdaoyi.model.query.PushConfigPageQuery;
import com.github.dingdaoyi.model.vo.PushConfigDetailVo;
import com.github.dingdaoyi.model.vo.PushConfigPageVo;
import com.github.dingdaoyi.service.PushConfigService;
import com.github.dingdaoyi.utils.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 推送配置服务实现
 * @author dingyunwei
 */
@Service
public class PushConfigServiceImpl extends ServiceImpl<PushConfigMapper, PushConfig> implements PushConfigService {

    @Override
    public PageResult<PushConfigPageVo> pageByQuery(PushConfigPageQuery query) {
        Page<PushConfigPageVo> page = PageHelper.page(query);
        Page<PushConfigPageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<PushConfigDetailVo> details(Integer id) {
        PushConfig config = baseMapper.selectById(id);
        if (config == null) {
            return Optional.empty();
        }
        PushConfigDetailVo vo = new PushConfigDetailVo();
        BeanUtil.copyProperties(config, vo);
        return Optional.of(vo);
    }

    @Override
    public List<PushConfig> listEnabled() {
        return baseMapper.listEnabled();
    }
}
