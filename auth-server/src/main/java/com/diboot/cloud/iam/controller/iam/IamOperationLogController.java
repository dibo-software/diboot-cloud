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
package com.diboot.cloud.iam.controller.iam;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.entity.IamLoginTrace;
import com.diboot.cloud.iam.handler.AsyncLogWorker;
import com.diboot.cloud.iam.service.IamOperationLogService;
import com.diboot.cloud.vo.IamLoginTraceVO;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.cloud.entity.IamOperationLog;
import com.diboot.core.vo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* 操作日志相关Controller
* @author JerryMa
* @version 1.0
* @date 2020-11-17
* Copyright © www.dibo.ltd
*/
@Slf4j
@RestController
@RequestMapping("/iam/operationLog")
public class IamOperationLogController extends BaseCrudRestController<IamOperationLog> {

    @Autowired
    private AsyncLogWorker asyncLogWorker;

    @Autowired
    private IamOperationLogService iamOperationLogService;

    /***
     * 新建操作日志
     * @param operationLog
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamOperationLog operationLog) throws Exception {
        asyncLogWorker.saveOperationLog(operationLog);
        return JsonResult.OK();
    }


    /***
     * 查询分页数据
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamOperationLog entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamOperationLog.class);
    }

    /***
     * 获取服务模块列表
     * @return
     * @throws Exception
     */
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/moduleList")
    public JsonResult getDictModuleList() throws Exception {
        List<IamOperationLog> operationLogList = iamOperationLogService.getEntityList(
                Wrappers.<IamOperationLog>lambdaQuery()
                .groupBy(IamOperationLog::getAppModule)
                .select(IamOperationLog::getAppModule)
        );
        List<String> appModuleList = new ArrayList<>();
        if (V.notEmpty(operationLogList)) {
            appModuleList = operationLogList.stream().map(IamOperationLog::getAppModule).collect(Collectors.toList());
        }
        return JsonResult.OK(appModuleList);
    }

}
