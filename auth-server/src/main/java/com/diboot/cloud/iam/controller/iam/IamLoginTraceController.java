package com.diboot.cloud.iam.controller.iam;

import com.diboot.cloud.annotation.BindPermission;
import com.diboot.cloud.annotation.Log;
import com.diboot.cloud.annotation.Operation;
import com.diboot.cloud.config.Cons;
import com.diboot.cloud.entity.IamLoginTrace;
import com.diboot.cloud.vo.IamLoginTraceVO;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* 登录日志
* @author Mazc
* @version 2.0
* @date 2020-11-16
* Copyright © dibo.ltd
*/
@RestController
@RequestMapping("/iam/loginTrace")
@BindPermission(name = "登录日志")
public class IamLoginTraceController extends BaseCrudRestController<IamLoginTrace> {
    private static final Logger log = LoggerFactory.getLogger(IamLoginTraceController.class);

    @Autowired
    private DictionaryService dictionaryService;

    /***
    * 查询分页数据
    * @return
    * @throws Exception
    */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamLoginTrace entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamLoginTraceVO.class);
    }

    /**
    * 加载更多数据
    * @return
    * @throws Exception
    */
    @GetMapping("/attachMore")
    public JsonResult attachMore(ModelMap modelMap) throws Exception {
        // 获取关联数据字典AUTH_TYPE的KV
        List<KeyValue> authTypeKvList = dictionaryService.getKeyValueList(Cons.DICTTYPE.AUTH_TYPE.name());
        modelMap.put("authTypeKvList", authTypeKvList);
        return JsonResult.OK(modelMap);
    }

}