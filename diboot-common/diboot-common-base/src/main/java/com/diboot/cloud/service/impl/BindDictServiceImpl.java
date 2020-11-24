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

import com.diboot.cloud.redis.RedisCons;
import com.diboot.core.service.BindDictService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典绑定的实现类
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/11/24
 */
@Service
public class BindDictServiceImpl implements BindDictService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String dictType) {
        if(V.isEmpty(voList)){
            return;
        }
        Map<String, Object> kvMap = getItemsKVMap(dictType);
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

    private Map<String, Object> getItemsKVMap(String dictType){
        List<KeyValue> keyValues = getKeyValueList(dictType);
        if(V.isEmpty(keyValues)){
            return Collections.emptyMap();
        }
        Map<String, Object> kvMap = new HashMap<>();
        for(KeyValue keyValue : keyValues){
            kvMap.put(keyValue.getK(), keyValue.getV());
        }
        return kvMap;
    }

}
