package com.github.dingdaoyi.iot.proto.impl;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.iot.proto.ProtocolInitialize;
import com.github.dingdaoyi.iot.proto.script.ScriptProtocolDecoder;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脚本协议初始化器
 * 支持多种脚本语言：JavaScript, Python, Groovy, Lua 等
 *
 * @author dingyunwei
 */
@Service
@Slf4j
public class ScriptProtocolInitialize implements ProtocolInitialize {

    @Resource
    private ResourceLoader resourceLoader;

    @Override
    public Map<String, ProtocolDecoder> initProtocol(List<Protocol> protocols) {
        Map<String, ProtocolDecoder> result = new HashMap<>();

        if (protocols == null || protocols.isEmpty()) {
            log.info("没有脚本协议需要初始化");
            return result;
        }

        log.info("开始初始化脚本协议，共{}个", protocols.size());

        for (Protocol protocol : protocols) {
            try {
                ProtocolDecoder decoder = createScriptDecoder(protocol);
                if (decoder != null) {
                    result.put(protocol.getProtoKey(), decoder);
                    log.info("脚本协议初始化成功: {} - {} ({})",
                        protocol.getName(), protocol.getProtoKey(), protocol.getMark());
                }
            } catch (Exception e) {
                log.error("脚本协议初始化失败: {} - {}, 错误: {}",
                    protocol.getName(), protocol.getProtoKey(), e.getMessage(), e);
            }
        }

        log.info("脚本协议初始化完成，成功加载{}个", result.size());
        return result;
    }

    /**
     * 创建脚本协议解析器
     *
     * @param protocol 协议配置
     * @return 协议解析器实例
     */
    private ProtocolDecoder createScriptDecoder(Protocol protocol) {
        // 优先使用 scriptContent 字段
        if (protocol.hasScriptContent()) {
            ScriptType scriptType = parseScriptType(protocol.getScriptLang());
            return createFromContent(protocol, protocol.getScriptContent(), scriptType);
        }

        // 其次从 url 字段指定的文件路径读取
        String scriptPath = protocol.getUrl();
        if (scriptPath != null && !scriptPath.trim().isEmpty()) {
            return createFromFile(protocol, scriptPath);
        }

        log.warn("脚本协议{}未配置脚本内容或文件路径", protocol.getProtoKey());
        return null;
    }

    /**
     * 从脚本内容创建解析器
     */
    private ProtocolDecoder createFromContent(Protocol protocol, String scriptContent, ScriptType scriptType) {
        try {
            log.info("加载脚本协议: {} (类型: {})", protocol.getProtoKey(), scriptType.getDisplayName());

            return new ScriptProtocolDecoder(
                protocol.getProtoKey(),
                protocol.getName(),
                scriptContent,
                scriptType
            );
        } catch (Exception e) {
            log.error("从内容创建脚本解析器失败: {}", protocol.getProtoKey(), e);
            return null;
        }
    }

    /**
     * 从文件创建解析器
     */
    private ProtocolDecoder createFromFile(Protocol protocol, String scriptPath) {
        try {
            Path path = Paths.get(scriptPath);
            if (!Files.exists(path)) {
                log.warn("脚本文件不存在: {}", scriptPath);
                return null;
            }

            String scriptContent = Files.readString(path, StandardCharsets.UTF_8);

            // 根据文件扩展名检测脚本类型
            ScriptType scriptType = ScriptType.fromExtension(getFileExtension(scriptPath));

            log.info("从文件加载脚本: {} (类型: {})", scriptPath, scriptType);

            return new ScriptProtocolDecoder(
                protocol.getProtoKey(),
                protocol.getName(),
                scriptContent,
                scriptType
            );
        } catch (IOException e) {
            log.error("读取脚本文件失败: {}", scriptPath, e);
            return null;
        }
    }

    /**
     * 解析脚本语言类型
     */
    private ScriptType parseScriptType(String scriptLang) {
        if (scriptLang == null || scriptLang.isEmpty()) {
            return ScriptType.JAVASCRIPT;
        }

        return switch (scriptLang.toLowerCase()) {
            case "js", "javascript" -> ScriptType.JAVASCRIPT;
            case "py", "python" -> ScriptType.PYTHON;
            case "groovy" -> ScriptType.GROOVY;
            case "lua" -> ScriptType.LUA;
            default -> ScriptType.JAVASCRIPT;
        };
    }

    /**
     * 检测脚本类型
     */
    private ScriptType detectScriptType(String protoKey, String scriptContent) {
        // 简单检测：检查关键字
        String trimmed = scriptContent.trim().toLowerCase();

        if (trimmed.contains("function ") || trimmed.contains("=>") ||
            trimmed.contains("const ") || trimmed.contains("let ") ||
            trimmed.contains("var ")) {
            return ScriptType.JAVASCRIPT;
        }

        if (trimmed.contains("def ") || trimmed.contains("import ") ||
            trimmed.contains("class ") && trimmed.contains(":")) {
            return ScriptType.PYTHON;
        }

        if (trimmed.contains("def ") && trimmed.contains("{")) {
            return ScriptType.GROOVY;
        }

        if (trimmed.contains("function ") || trimmed.contains("local ")) {
            return ScriptType.LUA;
        }

        // 默认使用 JavaScript
        log.warn("无法检测脚本类型，默认使用 JavaScript: {}", protoKey);
        return ScriptType.JAVASCRIPT;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * 脚本类型枚举
     */
    public enum ScriptType {
        JAVASCRIPT("js", "JavaScript"),
        PYTHON("py", "Python"),
        GROOVY("groovy", "Groovy"),
        LUA("lua", "Lua");

        private final String extension;
        private final String displayName;

        ScriptType(String extension, String displayName) {
            this.extension = extension;
            this.displayName = displayName;
        }

        public String getExtension() {
            return extension;
        }

        public String getDisplayName() {
            return displayName;
        }

        /**
         * 根据文件扩展名获取脚本类型
         */
        public static ScriptType fromExtension(String extension) {
            for (ScriptType type : values()) {
                if (type.extension.equalsIgnoreCase(extension)) {
                    return type;
                }
            }
            return JAVASCRIPT; // 默认
        }
    }
}