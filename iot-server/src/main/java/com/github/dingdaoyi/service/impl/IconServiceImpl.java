package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.service.ResourceUseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.Icon;
import com.github.dingdaoyi.mapper.IconMapper;
import com.github.dingdaoyi.service.IconService;
/**
 * @author dingyunwei
 */
@Service
public class IconServiceImpl extends ServiceImpl<IconMapper, Icon> implements IconService{

    @Resource
    private List<ResourceUseService<Icon>> iconUseServices;
    @Override
    public String path(Integer id) {
        return baseMapper.findPathById(id);
    }

    @Override
    public boolean removeById(Serializable id) {
        Icon icon = getById(id);
        if (icon == null) {
            return false;
        }
        for (ResourceUseService<Icon> iconUseService : iconUseServices) {
            if (iconUseService.isUsed(icon)) {
                throw new BusinessException(ResultCode.BAD_REQUEST,"图标在"+iconUseService.userTarget()+"中已使用,无法删除!");
            }
        }
        return super.removeById(id);
    }
}
