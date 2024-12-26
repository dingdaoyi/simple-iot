package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.dingdaoyi.entity.MessageReceive;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.model.ToEntity;
import com.github.dingdaoyi.model.enu.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物模型服务修改
 * @author dingyunwei
 */
@Data
public class MessageReceiveUpdateQuery implements ToEntity<MessageReceive> {

    /**
     * id
     */
    @Schema(description = "id")
    @NotNull(message = "修改id不能为空")
    private Integer id;

    /**
     * 接收者名称
     */
    @Schema(description = "接收者名称")
    private String name;

    /**
     * 接收对象
     */
    @Schema(description = "接收对象")
    private String receiver;

    /**
     * 通知类型
     */
    @Schema(description = "通知类型")
    private NotifyType notifyType;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Override
    public MessageReceive toEntity() {
        MessageReceive receive = new MessageReceive();
        receive.setId(id);
        receive.setName(name);
        receive.setReceiver(receiver);
        receive.setNotifyType(notifyType);
        receive.setRemark(remark);
        return receive;
    }
}
