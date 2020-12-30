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
package com.diboot.iam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

/**
 * 登录用户定义
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Getter @Setter
public class LoginUserDetail implements UserDetails {
    private static final long serialVersionUID = 100001L;

    private String username;
    /**
     * 显示名称
     */
    private String displayName;

    @JsonIgnore
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private List<GrantedAuthority> authorities;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 用户ID
     */
    private Long userId;

    public LoginUserDetail(){
    }

    public LoginUserDetail(String username, String password, boolean enabled, boolean accountNonLocked,
                           boolean accountNonExpired, boolean credentialsNonExpired, List<? extends GrantedAuthority> authorities,
                           String userType, Long userId, String displayName) {
        if (username != null && !"".equals(username) && password != null) {
            this.username = username;
            this.password = password;
            this.enabled = enabled;
            this.accountNonExpired = accountNonExpired;
            this.credentialsNonExpired = credentialsNonExpired;
            this.accountNonLocked = accountNonLocked;
            this.authorities = Collections.unmodifiableList(authorities);
            this.userType = userType;
            this.userId = userId;
            this.displayName = displayName;
        }
        else {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
    }

}
