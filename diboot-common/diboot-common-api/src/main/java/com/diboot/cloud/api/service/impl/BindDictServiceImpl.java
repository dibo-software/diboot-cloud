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
package com.diboot.cloud.api.service.impl;

import com.diboot.core.service.BindDictService;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典绑定的service实现
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Service
public class BindDictServiceImpl implements BindDictService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 从redis中读取字典并绑定
     * @param voList
     * @param setFieldName
     * @param getFieldName
     * @param type
     */
    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String type){
        if(V.isEmpty(voList)){
            return;
        }
        /*
        bindingFieldTo(voList)
                .link(Cons.FIELD_ITEM_NAME, setFieldName)
                .joinOn(getFieldName, Cons.FIELD_ITEM_VALUE)
                .andEQ(Cons.FIELD_TYPE, type)
                .andGT(Cons.FieldName.parentId.name(), 0)
                .bind();*/
    }

    /**
     * 获取keyValue集合
     * @param type
     * @return
     */
    @Override
    public List<KeyValue> getKeyValueList(String type) {
        /*
        // 构建查询条件
        Wrapper queryDictionary = new QueryWrapper<Dictionary>().lambda()
                .select(Dictionary::getItemName, Dictionary::getItemValue)
                .eq(Dictionary::getType, type)
                .gt(Dictionary::getParentId, 0)
                .orderByAsc(Dictionary::getSortId, Dictionary::getId);
        // 返回构建条件
        return getKeyValueList(queryDictionary);*/
        return null;
    }

}
