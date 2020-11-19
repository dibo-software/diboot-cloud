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

import com.diboot.cloud.common.entity.LoginUser;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.exception.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * IAM自定义扩展
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Service
public class IamCustomizeImpl implements IamCustomize {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseLoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        LoginUser user = (LoginUser)principal;
        return null;
    }

    /***
     * 对用户密码加密
     * @param iamAccount
     */
    @Override
    public void encryptPwd(IamAccount iamAccount) {
        if(Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())){
            String encryptedPwd = encryptPwd(iamAccount.getAuthSecret(), null);
            iamAccount.setAuthSecret(encryptedPwd);
        }
    }

    /***
     * 对用户密码加密
     * @param password
     * @param salt
     */
    @Override
    public String encryptPwd(String password, String salt) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void checkPermission(String permissionCode) throws PermissionException {

    }

    @Override
    public boolean checkCurrentUserHasRole(String role) {
        return true;
        //return IamSecurityUtils.getSubject().hasRole(role);
    }

    @Override
    public void clearAuthorizationCache(String username) {
        //IamSecurityUtils.clearAuthorizationCache(username);
    }

    @Override
    public void clearAllAuthorizationCache() {
        //IamSecurityUtils.clearAllAuthorizationCache();
    }

    @Override
    public boolean isEnablePermissionCheck() {
        return true;
    }

    @Override
    public String[] getOrignPermissionCodes(Method method) {
        return new String[0];
    }

}
