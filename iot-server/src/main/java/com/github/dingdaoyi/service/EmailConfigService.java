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
     * 获取启用的默认配置（内部使用，包含密码）
     */
    Optional<EmailConfig> getDefaultConfig();

    /**
     * 获取默认配置 VO（对外接口，密码脱敏）
     */
    Optional<EmailConfigVo> getDefaultConfigVo();

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