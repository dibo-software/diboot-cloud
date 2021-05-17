package com.example.controller;

import com.diboot.cloud.service.DictionaryApiService;
import com.diboot.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.diboot.core.dto.AttachMoreDTO;
import com.diboot.core.util.V;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.entity.ValidList;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.S;
import com.diboot.core.vo.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.parser.ParserCache;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用类接口相关Controller
 * @author JerryMa
 * @version 2.2.1
 * @date 2021/5/12
 * Copyright © diboot.com
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController extends BaseController {

    @Autowired(required = false)
    private DictionaryApiService dictionaryApiService;

    /**
     * 获取附加属性的通用kvList接口，用于初始化前端下拉框选项。
     * 如数据量过大，请勿调用此通用接口
     * @param attachMoreDTOList
     * @return
     */
    @PostMapping("/common/attachMore")
    public JsonResult attachMore(@Valid @RequestBody ValidList<AttachMoreDTO> attachMoreDTOList) {
        if(V.isEmpty(attachMoreDTOList)){
            return JsonResult.OK(Collections.emptyMap());
        }
        Map<String, Object> result = new HashMap<>(attachMoreDTOList.size());
        for (AttachMoreDTO attachMoreDTO : attachMoreDTOList) {
            AttachMoreDTO.REF_TYPE type = attachMoreDTO.getType();
            String targetKeyPrefix = S.toLowerCaseCamel(attachMoreDTO.getTarget());
            if (type.equals(AttachMoreDTO.REF_TYPE.D)) {
                List<KeyValue> keyValueList = null;
                JsonResult<List> jsonResult = dictionaryApiService.getKeyValueList(attachMoreDTO.getTarget());
                if(jsonResult.isOK() && jsonResult.getData() != null){
                    keyValueList = jsonResult.getData();
                }
                result.put(targetKeyPrefix + "KvList", keyValueList);
            }
            else if (type.equals(AttachMoreDTO.REF_TYPE.T)) {
                String entityClassName = S.capFirst(targetKeyPrefix);
                Class<?> entityClass = ParserCache.getEntityClassByClassName(entityClassName);
                if (V.isEmpty(entityClass)) {
                    log.warn("传递错误的实体类型：{}", attachMoreDTO.getTarget());
                    continue;
                }
                BaseService baseService = ContextHelper.getBaseServiceByEntity(entityClass);
                if(baseService == null){
                    log.warn("未找到实体类型{} 对应的Service定义", attachMoreDTO.getTarget());
                    continue;
                }
                String value = V.isEmpty(attachMoreDTO.getValue()) ? ContextHelper.getPrimaryKey(entityClass) : attachMoreDTO.getValue();
                String key = attachMoreDTO.getKey();
                if (V.isEmpty(key)) {
                    for (Field field : entityClass.getDeclaredFields()) {
                        if (V.equals(field.getType().getName(), String.class.getName())) {
                            key = field.getName();
                            break;
                        }
                    }
                }
                // 构建前端下拉框的初始化数据
                List<KeyValue> keyValueList = baseService.getKeyValueList(Wrappers.query().select(key, value));
                result.put(targetKeyPrefix + "KvList", keyValueList);
            }
            else {
                log.error("错误的加载绑定类型：{}", attachMoreDTO.getType());
            }
        }
        return JsonResult.OK(result);
    }
}