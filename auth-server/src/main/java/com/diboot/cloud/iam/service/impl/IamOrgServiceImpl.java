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
package com.diboot.cloud.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.cloud.entity.IamOrg;
import com.diboot.cloud.iam.mapper.IamOrgMapper;
import com.diboot.cloud.iam.service.IamOrgService;
import com.diboot.cloud.vo.IamOrgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* 组织机构相关Service实现
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamOrgServiceImpl extends BaseIamServiceImpl<IamOrgMapper, IamOrg> implements IamOrgService{

    @Override
    public boolean createEntity(IamOrg iamOrg){
        // 设置层级及公司ID及level
        enhanceIamOrg(iamOrg);
        return super.createEntity(iamOrg);
    }

    @Override
    public boolean updateEntity(IamOrg iamOrg){
        // 设置层级及公司ID及level
        enhanceIamOrg(iamOrg);
        return super.updateEntity(iamOrg);
    }

    /**
     * 增强IamOrg的属性
     * @param iamOrg
     */
    private void enhanceIamOrg(IamOrg iamOrg){
        // 设置层级及公司ID
        if (iamOrg.getParentId() != null && V.notEquals(iamOrg.getParentId(), 0L)) {
            IamOrg parentOrg = getEntity(iamOrg.getParentId());
            if (parentOrg != null) {
                // 设置层级
                int parentLevel = parentOrg.getLevel().intValue();
                iamOrg.setLevel(parentLevel + 1);
                // 设置公司ID
                if (V.equals(parentOrg.getParentId(), 0) || V.isEmpty(parentOrg.getParentId())) {
                    iamOrg.setTopOrgId(parentOrg.getId());
                }
                else {
                    iamOrg.setTopOrgId(parentOrg.getTopOrgId());
                }
            }
        }
    }

    @Override
    public List<Long> getChildOrgIds(Long rootOrgId) {
        List<IamOrgVO> childOrgs = getOrgTree(rootOrgId);
        if(V.notEmpty(childOrgs)){
            List<Long> childOrgIds = new ArrayList<>();
            extractIds(childOrgs, childOrgIds);
            return childOrgIds;
        }
        return Collections.emptyList();
    }

    @Override
    public List<IamOrgVO> getOrgTree(Long rootOrgId) {
        QueryWrapper<IamOrg> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(IamOrg::getSortId, IamOrg::getId);
        List<IamOrg> orgList = getEntityList(queryWrapper);
        if (V.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        List<IamOrgVO> orgVOList = BeanUtils.convertList(orgList, IamOrgVO.class);
        return BeanUtils.buildTree(orgVOList, rootOrgId);
    }

    @Override
    public void sortList(List<IamOrg> orgList) {
        if (V.isEmpty(orgList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "排序列表不能为空");
        }
        List<Long> sortIdList = new ArrayList();
        // 先将所有序号重新设置为自身当前id
        for (IamOrg item : orgList) {
            item.setSortId(item.getId());
            sortIdList.add(item.getSortId());
        }
        // 将序号列表倒序排序
        sortIdList = sortIdList.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // 整理需要更新的列表
        List<IamOrg> updateList = new ArrayList<>();
        for (int i=0; i<orgList.size(); i++) {
            IamOrg item = orgList.get(i);
            IamOrg updateItem = new IamOrg();
            updateItem.setId(item.getId());
            updateItem.setSortId(sortIdList.get(i));
            updateList.add(updateItem);
        }
        if (updateList.size() > 0) {
            super.updateBatchById(updateList);
        }
    }

    /**
     * 提取id
     * @param orgs
     */
    private void extractIds(List<IamOrgVO> orgs, List<Long> resultIds){
        if(V.isEmpty(orgs)){
            return;
        }
        for(IamOrgVO orgVO : orgs){
            resultIds.add(orgVO.getId());
            if(V.notEmpty(orgVO.getChildren())){
                extractIds(orgVO.getChildren(), resultIds);
            }
        }
    }

}