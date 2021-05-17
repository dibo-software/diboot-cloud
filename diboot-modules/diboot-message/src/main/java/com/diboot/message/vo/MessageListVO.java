/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.message.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.message.entity.Message;
import com.diboot.message.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 消息 ListVO定义
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageListVO extends Message {

    private static final long serialVersionUID = -8747685560582177392L;

    /**
     * 关联字典：DICT_MESSAGE_CHANNEL
     */
    @BindDict(type = DICT_MESSAGE_CHANNEL, field = "channel")
    private String channelLabel;

    /**
     * 关联字典：DICT_MESSAGE_STATUS
     */
    @BindDict(type = DICT_MESSAGE_STATUS, field = "status")
    private String statusLabel;

    /**
     * 关联字段：MessageTemplate.title
     */
    @BindField(entity = MessageTemplate.class, field = "title", condition = "this.template_id=id")
    private String messageTemplateTitle;
}
