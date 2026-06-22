package com.github.dingdaoyi.service.push;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;

/**
 * Guard for outgoing webhook endpoints to reduce SSRF risk.
 */
@Component
public class PushEndpointGuard {

    private final boolean allowPrivateEndpoints;

    public PushEndpointGuard(@Value("${simple-iot.push.allow-private-endpoints:false}") boolean allowPrivateEndpoints) {
        this.allowPrivateEndpoints = allowPrivateEndpoints;
    }

    public void validate(String url) {
        try {
            URI uri = URI.create(url);
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException("仅支持 HTTP/HTTPS 推送地址");
            }
            if (allowPrivateEndpoints) {
                return;
            }
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("推送地址无效");
            }
            InetAddress address = InetAddress.getByName(host);
            if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()) {
                throw new IllegalArgumentException("不允许推送到内网或本机地址，请配置 simple-iot.push.allow-private-endpoints=true 后仅在可信环境使用");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("推送地址解析失败: " + e.getMessage(), e);
        }
    }
}
