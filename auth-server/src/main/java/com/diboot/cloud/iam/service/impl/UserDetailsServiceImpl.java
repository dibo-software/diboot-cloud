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
package com.diboot.cloud.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.iam.cons.IAMConfig;
import com.diboot.cloud.entity.LoginUser;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.cloud.iam.handler.AsyncLogWorker;
import com.diboot.cloud.config.Cons;
import com.diboot.cloud.entity.IamAccount;
import com.diboot.cloud.entity.IamRole;
import com.diboot.cloud.entity.IamUser;
import com.diboot.cloud.iam.service.IamAccountService;
import com.diboot.cloud.iam.service.IamUserRoleService;
import com.diboot.cloud.iam.service.IamUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户账号与权限查询Service
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IamAccountService iamAccountService;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private AsyncLogWorker asyncLogWorker;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询账户
        LambdaQueryWrapper<IamAccount> queryWrapper = new QueryWrapper<IamAccount>().lambda()
                .select(IamAccount::getUserType, IamAccount::getUserId,
                        IamAccount::getAuthAccount, IamAccount::getAuthSecret, IamAccount::getStatus)
                .eq(IamAccount::getAuthAccount, username)
                .eq(IamAccount::getAuthType, Cons.DICTCODE_AUTH_TYPE.PWD.name());
        IamAccount account = iamAccountService.getSingleEntity(queryWrapper);
        if (account == null) {
            asyncLogWorker.saveLoginTrace(username, account, false);
            throw new UsernameNotFoundException(IAMConfig.LOGIN_MSG_ACCOUNT_ERROR);
        }
        if (Cons.DICTCODE_USER_STATUS.L.name().equals(account.getStatus())) {
            asyncLogWorker.saveLoginTrace(username, account, false);
            throw new LockedException(IAMConfig.LOGIN_MSG_ACCOUNT_LOCKED);
        }
        boolean enabled = Cons.DICTCODE_USER_STATUS.A.name().equals(account.getStatus());
        if (!enabled) {
            asyncLogWorker.saveLoginTrace(username, account, false);
            throw new DisabledException(IAMConfig.LOGIN_MSG_ACCOUNT_DISABLED);
        }
//        else if (DictEnums.USER_STATUS.A.name().equals(account.getStatus())) {
//            throw new AccountExpiredException(IAMConfig.LOGIN_MSG_ACCOUNT_EXPIRED);
//        }
//        else if (loginUser.isCredentialsNonExpired() != true) {
//            throw new CredentialsExpiredException(IAMConfig.LOGIN_MSG_CREDENTIALS_EXPIRED);
//        }
        // 初始化用户角色
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(account.getUserType(), account.getUserId());
        List<String> roleCodes = BeanUtils.collectToList(roleList, IamRole::getCode);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(V.notEmpty(roleCodes)){
            Collections.sort(roleCodes);
            for(String role : roleCodes){
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        boolean accountNonLocked = !Cons.DICTCODE_USER_STATUS.L.name().equals(account.getStatus());
        // 获取当前用户显示名
        IamUser iamUser = iamUserService.getEntity(account.getUserId());
        String displayName = iamUser != null? iamUser.getDisplayName() : null;
        // 构建登录用户
        return new LoginUser(account.getAuthAccount(), account.getAuthSecret(),
                enabled, accountNonLocked, true, true, authorities, account.getUserType(), account.getUserId(), displayName);
    }

}
