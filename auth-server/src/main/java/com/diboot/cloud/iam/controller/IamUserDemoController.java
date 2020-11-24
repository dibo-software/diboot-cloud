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
package com.diboot.cloud.iam.controller;

import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.core.vo.JsonResult;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.entity.IamUser;
import com.diboot.cloud.iam.service.IamUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IAM-User相关Controller
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@BindPermission(name = "用户IamUser", code = "IamUser")
@RestController
@RequestMapping("/iamUser")
public class IamUserDemoController {

    @Autowired
    private IamUserService iamUserService;

    @Log(businessObj = "系统用户", operation = Operation.LABEL_DETAIL)
    @BindPermission(name = "获取当前用户")
    @GetMapping("/get")
    public JsonResult<IamUser> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        LoginUserDetail user = (LoginUserDetail)principal;
        System.out.println("username= " + user.getUsername());
        IamUser iamUser = iamUserService.getEntity(1271753465195720705L);
        return JsonResult.OK(iamUser);
    }

    @BindPermission(name = "按ID获取用户")
    @GetMapping("/{id}")
    public JsonResult<IamUser> getUserById(@PathVariable("id")Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        LoginUserDetail user = (LoginUserDetail)principal;
        System.out.println("username= " + user.getUsername());
        IamUser iamUser = iamUserService.getEntity(id);
        return JsonResult.OK(iamUser);
    }
}
