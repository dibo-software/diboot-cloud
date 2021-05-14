package com.diboot.message.dto;

import com.diboot.message.entity.VariableData;
import com.diboot.message.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 消息传送的DTO
 * @author : uu
 * @version : v1.0
 * @Date 2021/5/14  08:21
 */
@Getter@Setter@Accessors(chain = true)
public class MessageTransferDTO implements Serializable {
    private static final long serialVersionUID = 2647125780368824120L;

    /**
     * message 消息体
     */
    @NotNull(message = "发送消息不能为空")
    private Message message;

    /**
     * 变量值
     */
    private VariableData variableData;
}
