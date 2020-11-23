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
package com.diboot.cloud.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.cloud.iam.service.RoleResourceCacheService;
import com.diboot.cloud.iam.util.IamHelper;
import com.diboot.cloud.util.IamSecurityUtils;
import com.diboot.core.binding.Binder;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.cloud.iam.service.IamExtensible;
import com.diboot.cloud.config.Cons;
import com.diboot.cloud.entity.IamRole;
import com.diboot.cloud.entity.IamUserRole;
import com.diboot.cloud.exception.PermissionException;
import com.diboot.cloud.iam.mapper.IamUserRoleMapper;
import com.diboot.cloud.iam.service.IamRoleService;
import com.diboot.cloud.iam.service.IamUserRoleService;
import com.diboot.cloud.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* 用户角色关联相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserRoleServiceImpl extends BaseIamServiceImpl<IamUserRoleMapper, IamUserRole> implements IamUserRoleService {

    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private RoleResourceCacheService roleResourceCacheService;
    // 扩展接口
    @Autowired(required = false)
    private IamExtensible iamExtensible;

    /**
     * 系统管理员的角色ID
     */
    private static Long ROLE_ID_SYSTEM_ADMIN = null;

    @Override
    public List<IamRole> getUserRoleList(String userType, Long userId) {
        return getUserRoleList(userType, userId, null);
    }

    @Override
    public List<IamRole> getUserRoleList(String userType, Long userId, Long extentionObjId) {
        List<IamUserRole> userRoleList = getEntityList(Wrappers.<IamUserRole>lambdaQuery()
                .select(IamUserRole::getRoleId)
                .eq(IamUserRole::getUserType, userType)
                .eq(IamUserRole::getUserId, userId)
        );
        if(V.isEmpty(userRoleList)){
            return Collections.emptyList();
        }
        List<Long> roleIds = BeanUtils.collectToList(userRoleList, IamUserRole::getRoleId);
        // 查询当前角色
        List<IamRole> roles = iamRoleService.getEntityList(Wrappers.<IamRole>lambdaQuery()
                .select(IamRole::getId, IamRole::getName, IamRole::getCode)
                .in(IamRole::getId, roleIds));
        // 加载扩展角色
        if(getIamExtensible() != null){
            List<IamRole> extRoles = getIamExtensible().getExtentionRoles(userType, userId, extentionObjId);
            if(V.notEmpty(extRoles)){
                roles.addAll(extRoles);
                roles = BeanUtils.distinctByKey(roles, IamRole::getId);
            }
        }
        return roles;
    }

    @Override
    public boolean createEntity(IamUserRole entity){
        Long superAdminRoleId = getSystemAdminRoleId();
        if(superAdminRoleId != null && superAdminRoleId.equals(entity.getRoleId())){
            checkSystemAdminIdentity();
        }
        boolean success = super.createEntity(entity);
        if(success){
            // 清空用户缓存
            roleResourceCacheService.addIntoPendingRefresh(entity.getUserType(), entity.getUserId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean createEntities(Collection entityList) {
        if (V.isEmpty(entityList)) {
            return true;
        }
        Long systemAdminRoleId = getSystemAdminRoleId();
        boolean hasSuperAdmin = false;
        String userType = null;
        Long userId = null;
        for(Object entity : entityList){
            IamUserRole iamUserRole = (IamUserRole)entity;
            if(systemAdminRoleId != null && systemAdminRoleId.equals(iamUserRole.getRoleId())){
                hasSuperAdmin = true;
            }
            if(userId == null){
                userType = iamUserRole.getUserType();
                userId = iamUserRole.getUserId();
            }
        }
        if(hasSuperAdmin){
            checkSystemAdminIdentity();
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            roleResourceCacheService.addIntoPendingRefresh(userType, userId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserRoleRelations(String userType, Long userId, List<Long> roleIds) {
        if(V.isEmpty(roleIds)){
            return true;
        }
        Long systemAdminRoleId = getSystemAdminRoleId();
        // 给用户赋予了系统管理员，需确保当前用户为系统管理员权限
        if(systemAdminRoleId != null && roleIds.contains(systemAdminRoleId)){
            checkSystemAdminIdentity();
        }
        List<IamUserRole> entityList = new ArrayList<>();
        for(Long roleId : roleIds){
            entityList.add(new IamUserRole(userType, userId, roleId));
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            roleResourceCacheService.addIntoPendingRefresh(userType, userId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserRoleRelations(String userType, Long userId, List<Long> roleIds) {
        if (V.isEmpty(roleIds)){
            return true;
        }
        // 需要先获取旧的角色列表，来进行系统管理员权限判定
        List<IamUserRole> oldUserRoleList = this.getEntityList(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserType, userType)
                        .eq(IamUserRole::getUserId, userId)
        );
        List oldRoleIds = new ArrayList();
        if (V.notEmpty(oldUserRoleList)){
            oldRoleIds = oldUserRoleList.stream()
                    .map(IamUserRole::getRoleId)
                    .collect(Collectors.toList());
        }

        Long systemAdminRoleId = getSystemAdminRoleId();
        // 给用户赋予了系统管理员，需确保当前用户为系统管理员权限
        if(systemAdminRoleId != null && (roleIds.contains(systemAdminRoleId) || oldRoleIds.contains(systemAdminRoleId))){
            checkSystemAdminIdentity();
        }

        // 删除旧的用户-角色关联关系
        this.deleteEntities(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserId, userId)
                        .eq(IamUserRole::getUserType, userType)
        );
        List<IamUserRole> entityList = new ArrayList<>();
        for(Long roleId : roleIds){
            entityList.add(new IamUserRole(userType, userId, roleId));
        }
        boolean success = super.createEntities(entityList);
        if(success){
            // 清空用户缓存
            roleResourceCacheService.addIntoPendingRefresh(userType, userId);
        }
        return success;
    }

    @Override
    public IamRoleVO buildRoleVo4FrontEnd(String userType, Long userId) {
        List<IamRoleVO> roleVOList = getAllRoleVOList(userType, userId);
        if (V.isEmpty(roleVOList)){
            return null;
        }
        // 组合为前端格式
        return IamHelper.buildRoleVo4FrontEnd(roleVOList);
    }

    @Override
    public List<IamRoleVO> getAllRoleVOList(String userType, Long userId) {
        List<IamRole> roleList = getUserRoleList(userType, userId);
        if (V.isEmpty(roleList)){
            return null;
        }
        return Binder.convertAndBindRelations(roleList, IamRoleVO.class);
    }

    /**
     * 获取Iam扩展实现
     * @return
     */
    @Override
    public IamExtensible getIamExtensible(){
        return iamExtensible;
    }

    /**
     * 获取系统管理员角色ID
     * @return
     */
    private Long getSystemAdminRoleId(){
        if(ROLE_ID_SYSTEM_ADMIN == null){
            LambdaQueryWrapper<IamRole> queryWrapper = new LambdaQueryWrapper<IamRole>()
                    .select(IamRole::getId)
                    .eq(IamRole::getCode, Cons.ROLE_SYSTEM_ADMIN);
            IamRole admin = iamRoleService.getSingleEntity(queryWrapper);
            if(admin != null){
                ROLE_ID_SYSTEM_ADMIN = admin.getId();
            }
        }
        return ROLE_ID_SYSTEM_ADMIN;
    }

    /**
     * 检查系统管理员身份
     */
    private void checkSystemAdminIdentity(){
        if(!IamSecurityUtils.checkCurrentUserHasRole(Cons.ROLE_SYSTEM_ADMIN)){
            throw new PermissionException("非系统管理员用户不可授予其他用户系统管理员权限！");
        }
    }

}
