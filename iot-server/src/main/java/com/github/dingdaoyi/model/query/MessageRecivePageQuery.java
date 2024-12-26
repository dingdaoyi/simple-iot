package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.model.enu.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备分页
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageRecivePageQuery extends PageQuery {
    /**
     * 设备编号
     */
    @Schema(description = "接收账号")
    private String receiver;

    @Schema(description = "通知类型")
    private NotifyType notifyType;

}
