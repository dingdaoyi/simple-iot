package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingyunwei
 */
public interface DictService extends IService<Dict>{


    /**
     * 获取code分类,dictKey为0的数据
     * @return
     */
    List<Dict> groupList();

    /**
     * 获取整个字典列表
     * @param group 分组名称
     * @return
     */
    List<Dict> listByCode(String group);
}
