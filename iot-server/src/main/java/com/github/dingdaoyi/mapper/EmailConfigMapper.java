package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.EmailConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

/**
 * 邮箱配置 Mapper
 * @author dingyunwei
 */
public interface EmailConfigMapper extends BaseMapper<EmailConfig> {

    /**
     * 获取启用的默认配置
     */
    Optional<EmailConfig> findDefaultConfig();

    /**
     * 清除默认配置（排除指定ID）
     */
    @Update("UPDATE tb_email_config SET is_default = false WHERE id != #{excludeId}")
    int clearDefault(@Param("excludeId") Integer excludeId);
}
