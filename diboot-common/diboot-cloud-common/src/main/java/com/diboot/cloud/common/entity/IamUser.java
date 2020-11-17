/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.cloud.common.entity;

import com.diboot.core.entity.BaseEntity;
import com.diboot.core.vo.KeyValue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
* 系统用户 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
public class IamUser extends BaseEntity {
    private static final long serialVersionUID = -8462352695775599715L;

    /**
     * 租户ID
     */
    private Long tenantId;

    // 组织ID
    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "用户编号不能为空")
    @Length(max=20, message="用户编号长度应小于50")
    private String userNum;

    // 真实姓名
    @NotNull(message = "真实姓名不能为空")
    @Length(max=50, message="真实姓名长度应小于50")
    private String realname;

    // 性别
    @NotNull(message = "性别不能为空")
    @Length(max=10, message="性别长度应小于10")
    private String gender;

    // 手机号
    @Length(max=20, message="手机号长度应小于20")
    private String mobilePhone;

    // Email
    @Length(max=50, message="Email长度应小于50")
    private String email;

    // 状态
    @NotNull(message = "状态不能为空")
    @Length(max=10, message="状态长度应小于10")
    private String status;

    // 头像
    private String avatarUrl;

    public String getDisplayName() {
        return this.realname;
    }

    /**
     * 附加对象，用于岗位等扩展
     */
    private KeyValue extentionObj;

}