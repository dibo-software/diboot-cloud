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
package com.diboot.cloud.iam.handler;

import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.vo.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义返回结果：没有登录或token过期时
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException, ServletException {
        Throwable cause = ex.getCause();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        String errorMsg = S.substringBefore(ex.getMessage(), ":");
        if(cause instanceof InvalidTokenException) {
            log.warn("无效的token: {}", ex.getMessage());
            errorMsg = "请检查 ("+errorMsg+")";

        }
        else{
            log.warn("无token的非法请求: {}", ex.getMessage());
            errorMsg = "请指定token ("+errorMsg+")";
        }
        JsonResult result = JsonResult.FAIL_INVALID_TOKEN(errorMsg);
        response.getWriter().write(JSON.stringify(result));
    }

}
