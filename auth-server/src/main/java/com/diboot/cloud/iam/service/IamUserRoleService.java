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
package com.diboot.cloud.iam.service;

import com.diboot.cloud.entity.IamRole;
import com.diboot.cloud.entity.IamUserRole;
import com.diboot.cloud.vo.IamRoleVO;

import java.util.List;

/**
* 用户角色关联相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
public interface IamUserRoleService extends BaseIamService<IamUserRole> {

    /**
     * 获取用户所有的全部角色
     * @param userType
     * @param userId
     * @return
     */
    List<IamRole> getUserRoleList(String userType, Long userId);

    /**
     * 获取用户所有的全部角色
     * @param userType
     * @param userId
     * @param extentionObjId 岗位等扩展对象id
     * @return
     */
    List<IamRole> getUserRoleList(String userType, Long userId, Long extentionObjId);

    /**
     * 批量创建用户-角色的关系
     * @param userType
     * @param userId
     * @param roleIds
     * @return
     */
    boolean createUserRoleRelations(String userType, Long userId, List<Long> roleIds);

    /***
     * 批量更新用户-角色的关系
     * @param userType
     * @param userId
     * @param roleIds
     * @return
     */
    boolean updateUserRoleRelations(String userType, Long userId, List<Long> roleIds);

    /**
     * 构建role-permission角色权限数据格式(合并role等)，用于前端适配
     * @param userType
     * @param userId
     * @return
     */
    IamRoleVO buildRoleVo4FrontEnd(String userType, Long userId);

    /***
     * 获取用户的所有角色列表（包括扩展的关联角色）
     * @param userType
     * @param userId
     * @return
     */
    List<IamRoleVO> getAllRoleVOList(String userType, Long userId);

    /**
     * 获取Iam扩展实现
     * @return
     */
    IamExtensible getIamExtensible();
}