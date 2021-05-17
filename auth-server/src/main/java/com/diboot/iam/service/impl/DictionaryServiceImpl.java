/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.config.Cons;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.iam.service.AuthServerCacheService;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.iam.mapper.DictionaryMapper;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典相关service实现
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
@Primary
@Service
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService, DictionaryServiceExtProvider {
    private static final Logger log = LoggerFactory.getLogger(DictionaryServiceImpl.class);
    @Autowired
    private AuthServerCacheService authServerCacheService;

    @Override
    public List<KeyValue> getKeyValueList(String type) {
        // 构建查询条件
        Wrapper queryDictionary = new QueryWrapper<Dictionary>().lambda()
                .select(Dictionary::getItemName, Dictionary::getItemValue)
                .eq(Dictionary::getType, type)
                .gt(Dictionary::getParentId, 0)
                .orderByAsc(Dictionary::getSortId, Dictionary::getId);
        // 返回构建条件
        return getKeyValueList(queryDictionary);
    }

    @Override
    public boolean existsDictType(String dictType) {
        return exists(Dictionary::getType, dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictAndChildren(DictionaryVO dictVO) {
        Dictionary dictionary = dictVO;
        dictionary.setIsDeletable(true)
                .setIsEditable(true);
        if(!super.createEntity(dictionary)){
            log.warn("新建数据字典定义失败，type="+dictVO.getType());
            return false;
        }
        List<Dictionary> children = dictVO.getChildren();
        this.buildSortId(children);
        if(V.notEmpty(children)){
            for(Dictionary dict : children){
                dict.setParentId(dictionary.getId())
                        .setType(dictionary.getType())
                        .setAppModule(dictionary.getAppModule())
                        .setIsDeletable(dictionary.getIsDeletable())
                        .setIsEditable(dictionary.getIsEditable());
            }
            // 批量保存
            boolean success = super.createEntities(children);
            if(!success){
                String errorMsg = "新建数据字典子项失败，type="+dictVO.getType();
                log.warn(errorMsg);
                throw new BusinessException(Status.FAIL_OPERATION, errorMsg);
            }
        }
        // 刷新缓存字典
        authServerCacheService.refreshDictionaryCache(dictVO.getType());
        return true;
    }

    @Override
    public List<Dictionary> getDictDefinitionList() {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getParentId, 0L);
        return getEntityList(queryWrapper);
    }

    @Override
    public List<DictionaryVO> getDictDefinitionVOList() {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getParentId, 0L);
        return getViewObjectList(queryWrapper, null, DictionaryVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictAndChildren(DictionaryVO dictVO) {
        Dictionary oldDictionary = super.getEntity(dictVO.getId());
        //将DictionaryVO转化为Dictionary
        Dictionary dictionary = dictVO;
        dictionary
                .setIsDeletable(oldDictionary.getIsDeletable())
                .setIsEditable(oldDictionary.getIsEditable());
        if(!super.updateEntity(dictionary)){
            log.warn("更新数据字典定义失败，type="+dictVO.getType());
            return false;
        }
        //获取原 子数据字典list
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Dictionary::getParentId, dictVO.getId());
        List<Dictionary> oldDictList = super.getEntityList(queryWrapper);
        List<Dictionary> newDictList = dictVO.getChildren();
        Set<Long> dictItemIds = new HashSet<>();
        this.buildSortId(newDictList);
        if(V.notEmpty(newDictList)){
            for(Dictionary dict : newDictList){
                dict
                        .setType(dictVO.getType())
                        .setParentId(dictVO.getId())
                        .setAppModule(dictionary.getAppModule())
                        .setIsDeletable(dictionary.getIsDeletable())
                        .setIsEditable(dictionary.getIsEditable());
                if(V.notEmpty(dict.getId())){
                    dictItemIds.add(dict.getId());
                    if(!super.updateEntity(dict)){
                        log.warn("更新字典子项失败，itemName=" + dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "更新字典子项异常");
                    }
                }
                else{
                    if(!super.createEntity(dict)){
                        log.warn("新建字典子项失败，itemName=" + dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "新建字典子项异常");
                    }
                }
            }
        }
        if(V.notEmpty(oldDictList)){
            for(Dictionary dict : oldDictList){
                if(!dictItemIds.contains(dict.getId())){
                    if(!super.deleteEntity(dict.getId())){
                        log.warn("删除子数据字典失败，itemName="+dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "删除字典子项异常");
                    }
                }
            }
        }
        // 刷新缓存字典
        authServerCacheService.refreshDictionaryCache(dictVO.getType());
        return true;
    }

    @Override
    public boolean deleteDictAndChildren(Long id) {
        Dictionary dictionary = getEntity(id);
        if(dictionary == null){
            throw new BusinessException(Status.FAIL_VALIDATION, "字典不存在！");
        }
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(Dictionary::getId, id)
                .or()
                .eq(Dictionary::getParentId, id);
        deleteEntities(queryWrapper);
        // 刷新缓存字典
        authServerCacheService.removeDictionaryCache(dictionary.getType());
        return true;
    }

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String type){
        if(V.isEmpty(voList)){
            return;
        }
        bindingFieldTo(voList)
                .link(Cons.FIELD_ITEM_NAME, setFieldName)
                .joinOn(getFieldName, Cons.COLUMN_ITEM_VALUE)
                .andEQ(Cons.FIELD_TYPE, type)
                .andGT(Cons.FieldName.parentId.name(), 0)
                .bind();
    }

    /***
     * 构建排序编号
     * @param dictList
     */
    private void buildSortId(List<Dictionary> dictList) {
        if (V.isEmpty(dictList)) {
            return;
        }
        for (int i = 0; i < dictList.size(); i++) {
            Dictionary dict = dictList.get(i);
            dict.setSortId(i);
        }
    }

}