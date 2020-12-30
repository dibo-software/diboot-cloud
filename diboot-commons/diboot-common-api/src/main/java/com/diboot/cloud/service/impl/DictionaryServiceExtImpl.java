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
package com.diboot.cloud.service.impl;

import com.diboot.cloud.service.DictionaryApiService;
import com.diboot.cloud.redis.RedisCons;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 字典绑定的实现类
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/24
 */
@Service
public class DictionaryServiceExtImpl implements DictionaryServiceExtProvider {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired(required = false)
    private DictionaryApiService dictionaryApiService;

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String dictType) {
        if(V.isEmpty(voList)){
            return;
        }
        List<KeyValue> keyValues = getKeyValueList(dictType);
        Map<String, Object> kvMap = BeanUtils.convertKeyValueList2Map(keyValues);
        for(Object obj : voList){
            String key = BeanUtils.getStringProperty(obj, getFieldName);
            Object val = kvMap.get(key);
            if(val != null){
                BeanUtils.setProperty(obj, setFieldName, val);
            }
        }
    }

    @Override
    public List<KeyValue> getKeyValueList(String dictType) {
        Object itemsObj = redisTemplate.opsForHash().get(RedisCons.KEY_DICTIONARY_MAP, dictType);
        if(itemsObj != null){
            return (List<KeyValue>)itemsObj;
        }
        return null;
    }

    @Override
    public boolean existsDictType(String dictType) {
        return redisTemplate.opsForHash().hasKey(RedisCons.KEY_DICTIONARY_MAP, dictType);
    }

    @Override
    public boolean createDictAndChildren(DictionaryVO dictionaryVO) {
        JsonResult jsonResult = dictionaryApiService.createDictAndChildren(dictionaryVO);
        return jsonResult.isOK();
    }

    @Override
    public List<Dictionary> getDictDefinitionList() {
        JsonResult jsonResult = dictionaryApiService.getDictDefinitionList();
        return (List<Dictionary>)jsonResult.getData();
    }

    @Override
    public List<DictionaryVO> getDictDefinitionVOList() {
        JsonResult jsonResult = dictionaryApiService.getDictDefinitionVOList();
        return (List<DictionaryVO>)jsonResult.getData();
    }

}
