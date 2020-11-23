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
package com.diboot.cloud.util;

import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.cloud.redis.RedisCons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * IAM 安全相关工具类
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/23
 */
@Slf4j
public class IamSecurityUtils {

    /**
     * 获取当前登录用户
     * @return
     */
    public static LoginUserDetail getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 认证信息可能为空，因此需要进行判断。
        if (Objects.nonNull(authentication)) {
            Object principal = authentication.getPrincipal();
            return (LoginUserDetail)principal;
        }
        log.warn("无法获取当前用户: authentication = null");
        return null;
    }

    /**
     * 当前用户是否有某角色
     * @param roleCode
     * @return
     */
    public static boolean checkCurrentUserHasRole(String roleCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 认证信息可能为空，因此需要进行判断。
        if (Objects.nonNull(authentication)) {
            if(!roleCode.startsWith(RedisCons.PREFIX_ROLE)){
                roleCode = RedisCons.PREFIX_ROLE + roleCode;
            }
            for(GrantedAuthority authority : authentication.getAuthorities()){
                if(authority.getAuthority().equals(roleCode)){
                    return true;
                }
            }
        }
        return false;
    }

}
