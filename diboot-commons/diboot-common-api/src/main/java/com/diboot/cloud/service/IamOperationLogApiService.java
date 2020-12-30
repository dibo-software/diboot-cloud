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
package com.diboot.cloud.service;

import com.diboot.cloud.config.FeignConfig;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.core.vo.JsonResult;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 远程服务调用示例
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@FeignClient(value = "auth-server", configuration = FeignConfig.class)
public interface IamOperationLogApiService {

    @PostMapping("/iam/operationLog/")
    JsonResult saveOperationLog(IamOperationLog operationLog);

}