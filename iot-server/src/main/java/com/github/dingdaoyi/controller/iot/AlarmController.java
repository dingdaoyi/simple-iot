package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.auth.AuthUtil;
import com.github.dingdaoyi.config.AuditLog;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.AlarmComment;
import com.github.dingdaoyi.entity.enu.AlarmSeverity;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.mapper.AlarmCommentMapper;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;
import com.github.dingdaoyi.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 告警控制器
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/alarm")
@Tag(name = "告警管理")
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmCommentMapper alarmCommentMapper;

    @GetMapping("{id}")
    @Operation(summary = "告警详情")
    public BaseResult<Alarm> details(@PathVariable Integer id) {
        Alarm alarm = alarmService.getById(id);
        if (alarm == null) {
            return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "告警不存在");
        }
        return BaseResult.success(alarm);
    }

    @PostMapping("page")
    @Operation(summary = "告警分页列表")
    public PageResult<AlarmPageVo> page(@RequestBody AlarmPageQuery query) {
        return alarmService.pageByQuery(query);
    }

    @PutMapping("{id}/acknowledge")
    @Operation(summary = "确认告警")
    public BaseResult<Boolean> acknowledge(@PathVariable Integer id,
                                            @RequestBody(required = false) Map<String, String> body) {
        Alarm alarm = alarmService.getById(id);
        if (alarm == null) {
            return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "告警不存在");
        }
        alarm.setStatus(AlarmStatus.ACKNOWLEDGED);
        boolean ok = alarmService.updateById(alarm);
        if (ok) {
            AlarmComment comment = new AlarmComment();
            comment.setAlarmId(id);
            comment.setAction("ACKNOWLEDGE");
            comment.setAuthor(AuthUtil.getUsername());
            comment.setComment(body != null ? body.getOrDefault("comment", "") : "");
            alarmCommentMapper.insert(comment);
        }
        return BaseResult.success(ok);
    }

    @PutMapping("{id}/clear")
    @Operation(summary = "清除告警")
    @AuditLog(action = "CLEAR", resource = "ALARM")
    public BaseResult<Boolean> clear(@PathVariable Integer id,
                                      @RequestBody(required = false) Map<String, String> body) {
        String clearBy = AuthUtil.getUsername();
        boolean ok = alarmService.clearAlarm(id, clearBy);
        if (ok) {
            AlarmComment comment = new AlarmComment();
            comment.setAlarmId(id);
            comment.setAction("CLEAR");
            comment.setAuthor(clearBy);
            comment.setComment(body != null ? body.getOrDefault("comment", "") : "");
            alarmCommentMapper.insert(comment);
        }
        return BaseResult.success(ok);
    }

    @PutMapping("{id}/escalate")
    @Operation(summary = "升级告警严重程度")
    @AuditLog(action = "ESCALATE", resource = "ALARM")
    public BaseResult<Boolean> escalate(@PathVariable Integer id,
                                         @RequestBody Map<String, String> body) {
        Alarm alarm = alarmService.getById(id);
        if (alarm == null) {
            return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "告警不存在");
        }
        String severity = body.get("severity");
        alarm.setSeverity(AlarmSeverity.valueOf(severity));
        boolean ok = alarmService.updateById(alarm);
        if (ok) {
            AlarmComment comment = new AlarmComment();
            comment.setAlarmId(id);
            comment.setAction("ESCALATE");
            comment.setAuthor(AuthUtil.getUsername());
            comment.setComment("升级为: " + severity + (body.containsKey("comment") ? " - " + body.get("comment") : ""));
            alarmCommentMapper.insert(comment);
        }
        return BaseResult.success(ok);
    }

    @PostMapping("{id}/comment")
    @Operation(summary = "添加告警评论")
    public BaseResult<Boolean> addComment(@PathVariable Integer id,
                                           @RequestBody Map<String, String> body) {
        AlarmComment comment = new AlarmComment();
        comment.setAlarmId(id);
        comment.setAction("COMMENT");
        comment.setAuthor(AuthUtil.getUsername());
        comment.setComment(body.getOrDefault("comment", ""));
        return BaseResult.success(alarmCommentMapper.insert(comment) > 0);
    }

    @GetMapping("{id}/comments")
    @Operation(summary = "告警评论列表")
    public BaseResult<List<AlarmComment>> listComments(@PathVariable Integer id) {
        return BaseResult.success(alarmCommentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AlarmComment>()
                        .eq(AlarmComment::getAlarmId, id)
                        .orderByDesc(AlarmComment::getCreateTime)));
    }

    @GetMapping("statistics")
    @Operation(summary = "告警统计")
    public BaseResult<Map<String, Object>> statistics() {
        long activeCount = alarmService.countActiveAlarms();
        return BaseResult.success(Map.of(
            "activeCount", activeCount,
            "severityOptions", Arrays.stream(AlarmSeverity.values())
                .map(s -> Map.of("value", s.name(), "label", s.getName(), "color", s.getColor()))
                .toList(),
            "statusOptions", Arrays.stream(AlarmStatus.values())
                .map(s -> Map.of("value", s.name(), "label", s.getName()))
                .toList()
        ));
    }

    @GetMapping("export")
    @Operation(summary = "导出告警列表Excel")
    public void export(jakarta.servlet.http.HttpServletResponse response) {
        List<Alarm> alarms = alarmService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Alarm>()
                .orderByDesc(Alarm::getStartTs)
                .last("LIMIT 1000"));
        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.addHeaderAlias("id", "ID");
        writer.addHeaderAlias("alarmType", "告警类型");
        writer.addHeaderAlias("alarmName", "告警名称");
        writer.addHeaderAlias("severity", "严重程度");
        writer.addHeaderAlias("status", "状态");
        writer.addHeaderAlias("message", "告警消息");
        writer.addHeaderAlias("deviceKey", "设备Key");
        writer.addHeaderAlias("deviceName", "设备名称");
        writer.addHeaderAlias("startTs", "开始时间");
        writer.addHeaderAlias("clearTs", "清除时间");
        writer.write(alarms, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=alarms.xlsx");
        try (java.io.OutputStream out = response.getOutputStream()) {
            writer.flush(out, true);
        } catch (java.io.IOException e) {
            throw new com.github.dingdaoyi.core.exception.BusinessException(ResultCode.BAD_REQUEST, "导出失败");
        }
    }
}
