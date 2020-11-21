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
package com.example.controller;

import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.api.service.IamUserApiService;
import com.diboot.cloud.entity.LoginUser;
import com.diboot.cloud.util.IamSecurityUtils;
import com.diboot.core.vo.JsonResult;
import com.diboot.cloud.entity.IamUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试controller
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@BindPermission(name = "测试", code = "Hello")
@RestController
public class HelloController {

    @Autowired(required = false)
    private IamUserApiService iamUserRemoteService;

    @Log(businessObj = "Hello", operation = "say hello .")
    @BindPermission(name = "say hello", code = "hello")
    @GetMapping("/hello")
    public JsonResult hello() {
        LoginUser loginUser = IamSecurityUtils.getCurrentUser();
        return JsonResult.OK(loginUser);
    }

    //@PreAuthorize("hasAnyRole('SYS_ADMIN', 'ROLE_SYS_ADMIN')")
    @GetMapping("/hellov/{id}/t")
    public JsonResult admin(@PathVariable("id")Long id) {
        LoginUser loginUser = IamSecurityUtils.getCurrentUser();
        return JsonResult.OK(loginUser);
    }

    @GetMapping("/test")
    public String test() {
        JsonResult<IamUser> jsonResult = iamUserRemoteService.getUser();
        IamUser iamUser = jsonResult.getData();
        String info = iamUser == null? "" : iamUser.toString();
        return "Hello OpenFeign result： " + info;
    }

    @GetMapping("/anon/test")
    public String anontest() {
        return "Hello anon user";
    }

}
