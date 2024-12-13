package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.Dict;
import com.github.dingdaoyi.mapper.DictMapper;
import com.github.dingdaoyi.service.DictService;

import java.util.List;

/**
 * @author dingyunwei
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> groupList() {
        return list(Wrappers
                .<Dict>lambdaQuery()
                .eq(Dict::getLabel, "-1")
                .orderByAsc(Dict::getSort));
    }

    @Override
    public List<Dict> listByCode(String group) {
        return list(Wrappers
                .lambdaQuery(Dict.class)
                .eq(Dict::getDictGroup, group)
                .not(w -> w.eq(Dict::getLabel, "-1"))
                .orderByAsc(Dict::getSort)
                .select(Dict::getLabel,Dict::getValue, Dict::getSort));
    }
}
