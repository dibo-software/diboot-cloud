package com.diboot.message.controller;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.Operation;
import com.diboot.message.dto.MessageTemplateDTO;
import com.diboot.message.entity.MessageTemplate;
import com.diboot.message.service.MessageTemplateService;
import com.diboot.message.vo.MessageTemplateDetailVO;
import com.diboot.message.vo.MessageTemplateListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息模版 相关Controller
 *
 * @author uu
 * @version 1.0
 * @date 2021-02-18
 * @Copyright © diboot.com
 */
@RestController
@RequestMapping("/messageTemplate")
@BindPermission(name = "消息模版")
@Slf4j
public class MessageTemplateController extends BaseCrudRestController<MessageTemplate> {
    @Autowired
    private MessageTemplateService messageTemplateService;

    /***
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(MessageTemplateDTO queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, MessageTemplateListVO.class);
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
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception {
        return super.getViewObject(id, MessageTemplateDetailVO.class);
    }

    /***
     * 创建资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_CREATE)
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody MessageTemplate entity) throws Exception {
        return super.createEntity(entity);
    }

    /***
     * 根据ID更新资源对象
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_UPDATE)
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody MessageTemplate entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /***
     * 根据id删除资源对象
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = Operation.LABEL_DELETE)
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 检查是否有重复的code
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/checkTempCodeDuplicate")
    public JsonResult checkTempCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) throws Exception {
        messageTemplateService.existCode(id, code);
        return JsonResult.OK();
    }

    /**
     * 加载更多
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore() throws Exception {
        Map<String, Object> result = new HashMap<>(8);
        // 加载模版变量
        result.put("templateTemplateVariableList", messageTemplateService.getTemplateTemplateVariableList());
        return JsonResult.OK(result);
    }

} 