package com.github.dingdaoyi.config.satoken;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dyw
 */
@ConfigurationProperties(prefix = "sa-token.sso")
@Data
public class SsoProperties {

    /**
     * 需要跳过的路由
     */
    private List<String> skipUrl=new ArrayList<>();

}
