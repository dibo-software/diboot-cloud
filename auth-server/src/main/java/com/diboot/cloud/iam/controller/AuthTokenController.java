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

import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.cloud.iam.service.IamUserRoleService;
import com.diboot.cloud.util.IamSecurityUtils;
import com.diboot.cloud.vo.IamRoleVO;
import com.diboot.core.vo.JsonResult;
import com.diboot.cloud.iam.handler.AsyncLogWorker;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2申请token相关Controller
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@RestController
@RequestMapping("/oauth")
public class AuthTokenController {

    @Autowired
    private KeyPair keyPair;
    private static JSONObject PUBLIC_KEY = null;

    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private ConsumerTokenServices consumerTokenServices;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private AsyncLogWorker asyncLogWorker;

    /**
     * Oauth2登录申请token
     */
    @PostMapping("/token")
    public JsonResult applyAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws Exception{
        // 构建token结果
        OAuth2AccessToken accessToken = null;
        String userType = null, username = parameters.get("username");
        Long userId = 0L;
        try{
            // 构建token结果
            accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
            // 获取用户信息
            Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
            userType = (String) additionalInfo.get("userType");
            userId = (Long) additionalInfo.get("userId");
        }
        catch (HttpRequestMethodNotSupportedException nse){
            throw nse;
        }
        catch (Exception e){
            asyncLogWorker.saveLoginTrace(username, userType, userId, false);
            throw e;
        }
        /*
        String token = accessToken.getValue();
        LoginUserDetail loginUser = null;
        if(IamUser.class.getSimpleName().equalsIgnoreCase(userType)){
            loginUser = iamUserService.getEntity(userId);
        }
        else{
            //其他用户类型
        }
        //redisTemplate.opsForValue().set(token, loginUser, accessToken.getExpiresIn(), TimeUnit.SECONDS);
        */
        asyncLogWorker.saveLoginTrace(username, userType, userId, true);
        return JsonResult.OK(accessToken);
    }

    /**
     * 注销token
     * @param access_token
     * @return
     */
    @DeleteMapping("/token")
    public JsonResult revokeToken(String access_token) {
        boolean success = consumerTokenServices.revokeToken(access_token);
        return JsonResult.OK().msg(success? "注销成功" : "注销失败");
    }

    /**
     * 获取用户角色权限信息
     * @return
     */
    @GetMapping("/userInfo")
    public JsonResult getUserInfo(){
        Map<String, Object> data = new HashMap<>();
        // 获取当前登录用户对象
        LoginUserDetail currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser == null){
            return JsonResult.OK();
        }
        data.put("name", currentUser.getDisplayName());
        // 角色权限数据
        IamRoleVO roleVO = iamUserRoleService.buildRoleVo4FrontEnd(currentUser.getUserType(), currentUser.getUserId());
        data.put("role", roleVO);
        return JsonResult.OK(data);
    }

    /**
     * 获取RSA 公钥
     * @return
     */
    @GetMapping("/token_key")
    public Map<String, Object> getPublicKey() {
        if(PUBLIC_KEY != null){
            return PUBLIC_KEY;
        }
        // 初始化PublicKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        PUBLIC_KEY = new JWKSet(key).toJSONObject();
        return PUBLIC_KEY;
    }

}
