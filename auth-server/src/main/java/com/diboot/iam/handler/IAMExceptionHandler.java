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
package com.diboot.iam.handler;

import com.diboot.core.handler.DefaultExceptionHandler;
import com.diboot.core.vo.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常统一处理的默认实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/09
 */
@ControllerAdvice
public class IAMExceptionHandler extends DefaultExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(IAMExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public JsonResult handleOauth2(OAuth2Exception e) {
        return JsonResult.FAIL_EXCEPTION(e.getMessage());
    }

}
