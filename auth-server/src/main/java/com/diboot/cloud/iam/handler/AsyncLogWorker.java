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
package com.diboot.cloud.iam.handler;

import com.diboot.cloud.config.Cons;
import com.diboot.cloud.entity.IamAccount;
import com.diboot.cloud.entity.IamLoginTrace;
import com.diboot.cloud.entity.IamOperationLog;
import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.cloud.iam.service.IamLoginTraceService;
import com.diboot.cloud.iam.service.IamOperationLogService;
import com.diboot.cloud.util.IamSecurityUtils;
import com.diboot.core.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 异步相关处理
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/09/21
 */
@Slf4j
@Async
@Component
public class AsyncLogWorker {
    @Autowired
    private IamLoginTraceService iamLoginTraceService;
    @Autowired
    private IamOperationLogService iamOperationLogService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 保存登录日志
     * @param account
     * @param isSuccess
     */
    public void saveLoginTrace(String username, IamAccount account, boolean isSuccess){
        String userType = null;
        Long userId = 0L;
        if(account != null){
            userType = account.getUserType();
            userId = account.getUserId();
        }
        saveLoginTrace(username, userType, userId, isSuccess);
    }

    /**
     * 保存登录日志
     * @param loginTrace
     */
    /**
     * 保存登录日志
     * @param userType
     * @param isSuccess
     */
    public void saveLoginTrace(String username, String userType, Long userId, boolean isSuccess){
        try{
            IamLoginTrace loginTrace = new IamLoginTrace();
            loginTrace.setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name()).setAuthAccount(username).setSuccess(isSuccess);
            loginTrace.setUserType(userType).setUserId(userId);
            // 记录客户端信息
            String userAgent = HttpHelper.getUserAgent(request);
            String ipAddress = HttpHelper.getRequestIp(request);
            loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
            iamLoginTraceService.createEntity(loginTrace);
        }
        catch (Exception e){
            log.error("保存登录日志异常", e);
        }
    }

    /**
     * 保存操作日志
     * @param operationLog
     */
    public void saveOperationLog(IamOperationLog operationLog) {
        try{
            // 操作用户信息
            LoginUserDetail loginUserDetail = IamSecurityUtils.getCurrentUser();
            if(loginUserDetail != null){
                operationLog.setUserType(loginUserDetail.getClass().getSimpleName())
                        .setUserId(loginUserDetail.getUserId())
                        .setUserRealname(loginUserDetail.getDisplayName());
            }
            iamOperationLogService.createEntity(operationLog);
        }
        catch (Exception e){
            log.error("保存操作日志异常", e);
        }
    }

}
