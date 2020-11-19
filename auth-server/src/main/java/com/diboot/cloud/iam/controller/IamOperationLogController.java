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

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.annotation.process.AsyncWorker;
import com.diboot.iam.entity.IamOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* 操作日志相关Controller
* @author JerryMa
* @version 1.0
* @date 2020-11-17
* Copyright © MyCompany
*/
@Slf4j
@RestController
@RequestMapping("/iam/operationLog")
public class IamOperationLogController extends BaseCrudRestController<IamOperationLog> {

    @Autowired
    private AsyncWorker asyncWorker;

    /***
     * 新建操作日志
     * @param operationLog
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamOperationLog operationLog) throws Exception {
        com.diboot.iam.entity.IamOperationLog iamOperationLog = new com.diboot.iam.entity.IamOperationLog();
        BeanUtils.copyProperties(operationLog, iamOperationLog);
        asyncWorker.saveOperationLog(iamOperationLog);
        return JsonResult.OK();
    }

}
