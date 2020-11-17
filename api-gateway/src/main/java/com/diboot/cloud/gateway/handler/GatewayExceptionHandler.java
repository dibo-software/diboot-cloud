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
package com.diboot.cloud.gateway.handler;

import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异常统一处理的实现
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/04
 */
@Slf4j
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GatewayExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = this.getError(request);
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        HttpStatus errorStatus = error instanceof ResponseStatusException ? ((ResponseStatusException)error).getStatus() :
                responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        // 基于状态码构建error返回信息
        Integer code = null;
        String errorMsg = null;
        if(HttpStatus.SERVICE_UNAVAILABLE.equals(errorStatus)){
            errorMsg = "服务不可用: ";
            code = Status.FAIL_SERVICE_UNAVAILABLE.code();
        }
        else if(HttpStatus.REQUEST_TIMEOUT.equals(errorStatus)){
            errorMsg = "请求连接超时: ";
            code = Status.FAIL_REQUEST_TIMEOUT.code();
        }
        else{
            errorMsg = "请求处理异常: ";
            code = Status.FAIL_EXCEPTION.code();
        }
        errorMsg += errorStatus.getReasonPhrase();

        Map<String, Object> errorAttributes = new LinkedHashMap();
        errorAttributes.put("status", errorStatus.value());
        errorAttributes.put("code", code);
        errorAttributes.put("msg", errorMsg);
        errorAttributes.put("data", null);
        //errorAttributes.put("requestId", request.exchange().getRequest().getId());

        return errorAttributes;
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return (int) errorAttributes.get("code");
    }

}
