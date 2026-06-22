package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterScriptConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 脚本过滤节点。
 * 使用受限 GraalVM JavaScript 上下文执行返回 true/false 的过滤脚本。
 *
 * @author dingyunwei
 */
@Component
public class ScriptFilterNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.FILTER_SCRIPT;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        FilterScriptConfig cfg = (FilterScriptConfig) config;

        if (cfg == null || StringUtils.isBlank(cfg.getScript())) {
            return NodeResult.fail("未配置脚本");
        }
        String language = StringUtils.defaultIfBlank(cfg.getLanguage(), "javascript").toLowerCase();
        if (!"javascript".equals(language) && !"js".equals(language)) {
            return NodeResult.fail("暂仅支持 JavaScript 脚本过滤");
        }

        try (Context jsContext = Context.newBuilder("js")
                .allowAllAccess(false)
                .option("engine.WarnInterpreterOnly", "false")
                .build()) {
            Value function = jsContext.eval("js", "(function(msg, device, enriched, ctx) {\n" + cfg.getScript() + "\n})");
            Value value = function.execute(
                    toJsObject(buildMessage(context)),
                    toJsObject(buildDevice(context)),
                    toJsObject(new LinkedHashMap<>(context.getEnrichedData())),
                    toJsObject(buildContext(context))
            );
            boolean passed = value != null && !value.isNull() && value.asBoolean();
            return new NodeResult(true, passed ? "True" : "False", "脚本返回 " + passed, Map.of(
                    "result", passed,
                    "language", "javascript"
            ));
        } catch (PolyglotException e) {
            return NodeResult.fail("脚本执行失败: " + e.getMessage());
        } catch (Exception e) {
            return NodeResult.fail("脚本执行失败: " + e.getMessage());
        }
    }

    private Map<String, Object> buildMessage(RuleContext context) {
        Map<String, Object> msg = context.getAllProperties().stream()
                .collect(Collectors.toMap(
                        DeviceData::getIdentifier,
                        DeviceData::getValue,
                        (left, right) -> right,
                        LinkedHashMap::new
                ));
        context.getEventData().ifPresent(eventData -> {
            msg.put("eventIdentifier", eventData.getIdentifier());
            Map<String, Object> params = eventData.getParams().stream()
                    .collect(Collectors.toMap(
                            DeviceData::getIdentifier,
                            DeviceData::getValue,
                            (left, right) -> right,
                            LinkedHashMap::new
                    ));
            msg.put("eventParams", params);
        });
        msg.put("messageType", context.getMessageType() == null ? null : context.getMessageType().name());
        return msg;
    }

    private Map<String, Object> buildDevice(RuleContext context) {
        Map<String, Object> device = new LinkedHashMap<>();
        device.put("deviceKey", context.getDeviceKey());
        device.put("deviceId", context.getDeviceId());
        device.put("deviceName", context.getDeviceName());
        return device;
    }

    private Map<String, Object> buildContext(RuleContext context) {
        Map<String, Object> ctx = new LinkedHashMap<>();
        ctx.put("device", buildDevice(context));
        ctx.put("msg", buildMessage(context));
        ctx.put("enriched", new LinkedHashMap<>(context.getEnrichedData()));
        ctx.put("eventTime", context.getEventTime());
        return ctx;
    }

    @SuppressWarnings("unchecked")
    private Object toJsObject(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> converted = new LinkedHashMap<>();
            map.forEach((key, item) -> converted.put(String.valueOf(key), toJsObject(item)));
            return ProxyObject.fromMap(converted);
        }
        return value;
    }
}
