package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.EmailConfig;
import com.github.dingdaoyi.mapper.EmailConfigMapper;
import com.github.dingdaoyi.model.query.EmailConfigAddQuery;
import com.github.dingdaoyi.model.query.EmailConfigPageQuery;
import com.github.dingdaoyi.model.query.EmailConfigUpdateQuery;
import com.github.dingdaoyi.model.vo.EmailConfigVo;
import com.github.dingdaoyi.service.EmailConfigService;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 邮箱配置服务实现
 * @author dingyunwei
 */
@Service
public class EmailConfigServiceImpl extends ServiceImpl<EmailConfigMapper, EmailConfig> implements EmailConfigService {

    @Override
    public PageResult<EmailConfigVo> pageByQuery(EmailConfigPageQuery query) {
        Page<EmailConfig> page = PageHelper.page(query);
        Page<EmailConfig> result = baseMapper.selectPage(page, Wrappers.<EmailConfig>lambdaQuery()
            .like(query.getName() != null, EmailConfig::getName, query.getName())
            .eq(query.getStatus() != null, EmailConfig::getStatus, query.getStatus())
            .orderByDesc(EmailConfig::getIsDefault)
            .orderByDesc(EmailConfig::getCreateTime)
        );
        // 转换为 VO，脱敏密码
        List<EmailConfigVo> voList = result.getRecords().stream()
            .map(EmailConfigVo::build)
            .toList();
        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<EmailConfigVo> getDefaultConfig() {
        return baseMapper.findDefaultConfig().map(EmailConfigVo::build);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Integer id) {
        // 清除其他默认配置
        baseMapper.clearDefault(id);
        // 设置当前配置为默认
        update(Wrappers.<EmailConfig>lambdaUpdate()
            .eq(EmailConfig::getId, id)
            .set(EmailConfig::getIsDefault, true)
        );
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        update(Wrappers.<EmailConfig>lambdaUpdate()
            .eq(EmailConfig::getId, id)
            .set(EmailConfig::getStatus, status)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(EmailConfigAddQuery query) {
        EmailConfig config = query.toEntity();
        // 如果是第一个配置，自动设为默认
        long count = count();
        if (count == 0) {
            config.setIsDefault(true);
        }
        return save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(EmailConfigUpdateQuery query) {
        EmailConfig config = query.toEntity();
        return updateById(config);
    }
}
