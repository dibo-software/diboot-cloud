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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.iam.handler.AsyncLogWorker;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.vo.JsonResult;
import com.diboot.cloud.entity.IamOperationLog;
import com.diboot.core.vo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    /***
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamOperationLog entity, Pagination pagination) throws Exception{
        QueryWrapper<IamOperationLog> queryWrapper = super.buildQueryWrapper(entity);
        Integer status = getInteger("status");
        if(status != null){
            if(status.intValue() == 0){
                queryWrapper.eq("status_code", 0);
            }
            else{
                queryWrapper.gt("status_code", 0);
            }
        }
        return super.getEntityListWithPaging(queryWrapper, pagination);
    }

    /***
     * 根据资源id查询ViewObject
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DETAIL)
    @BindPermission(name = Operation.LABEL_DETAIL, code = Operation.CODE_DETAIL)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception{
        IamOperationLog operationLog = super.getEntity(id);
        return JsonResult.OK(operationLog);
    }

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

}
