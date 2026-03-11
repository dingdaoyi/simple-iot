package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.EmailConfig;
import com.github.dingdaoyi.model.query.EmailConfigPageQuery;
import com.github.dingdaoyi.model.query.EmailConfigAddQuery;
import com.github.dingdaoyi.model.query.EmailConfigUpdateQuery;
import com.github.dingdaoyi.model.vo.EmailConfigVo;

import java.util.Optional;

/**
 * 邮箱配置服务接口
 * @author dingyunwei
 */
public interface EmailConfigService extends IService<EmailConfig> {

    /**
     * 分页查询
     */
    PageResult<EmailConfigVo> pageByQuery(EmailConfigPageQuery query);

    /**
     * 获取启用的默认配置
     */
    Optional<EmailConfigVo> getDefaultConfig();

    /**
     * 设置默认配置
     */
    void setDefault(Integer id);

    /**
     * 更新状态
     */
    void updateStatus(Integer id, Integer status);

    /**
     * 保存配置
     */
    boolean save(EmailConfigAddQuery query);

    /**
     * 更新配置
     */
    boolean update(EmailConfigUpdateQuery query);
}