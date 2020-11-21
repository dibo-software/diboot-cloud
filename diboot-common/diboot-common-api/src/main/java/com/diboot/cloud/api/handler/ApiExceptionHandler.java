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
package com.diboot.cloud.api.handler;

import com.diboot.core.handler.DefaultExceptionHandler;
import com.diboot.core.vo.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常统一处理的默认实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/04
 */
@ControllerAdvice
public class ApiExceptionHandler extends DefaultExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public JsonResult handle404(NoHandlerFoundException e) {
        log.info("Resource Not found, RequestURL: {}, HttpMethod: {}, Headers: {}", e.getRequestURL(),
                e.getHttpMethod(), e.getHeaders());
        log.error(e.getMessage(), e);

        return JsonResult.FAIL_NOT_FOUND("请求URL不存在");
    }

}
