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

import com.diboot.cloud.config.Cons;
import com.diboot.cloud.entity.IamRole;
import com.diboot.cloud.entity.LoginUserDetail;
import com.diboot.cloud.iam.service.IamRoleResourceService;
import com.diboot.cloud.iam.service.IamUserRoleService;
import com.diboot.cloud.iam.service.RoleResourceCacheService;
import com.diboot.cloud.redis.RedisCons;
import com.diboot.cloud.vo.ResourceRoleVO;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色资源缓存Service
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/23
 */
@Slf4j
@Service
public class RoleResourceCacheServiceImpl implements RoleResourceCacheService {

    @Autowired
    private IamRoleResourceService iamRoleResourceService;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 刷新资源角色映射缓存
     * @return
     */
    @Override
    public boolean refreshResourceRolesCache(){
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        // 获取全部资源-角色关联
        List<ResourceRoleVO> voList = iamRoleResourceService.getAllResourceRoleVOList();
        if(V.notEmpty(voList)){
            for(ResourceRoleVO vo : voList){
                // 忽略 无URL
                if(V.isEmpty(vo.getApiSet())){
                    continue;
                }
                List<String> roleCodes = vo.getRoleCodes();
                if(V.isEmpty(vo.getRoleCodes())){
                    roleCodes = Collections.emptyList();
                }
                else{
                    roleCodes = roleCodes.stream().map(i -> i = RedisCons.PREFIX_ROLE + i).collect(Collectors.toList());
                }
                // 组装
                String[] apiArray = S.split(vo.getApiSet());
                for(String api : apiArray){
                    String httpMethod = S.substringBefore(api,":");
                    String fullUri = httpMethod.toUpperCase() + ":/" + vo.getAppModule() + S.substringAfter(api, ":");
                    resourceRolesMap.put(fullUri, roleCodes);
                }
            }
        }
        log.info("初始化资源角色缓存完成, 共加载 {} 项", resourceRolesMap.size());
        log.debug("资源-角色匹配: {}", resourceRolesMap);
        redisTemplate.opsForHash().putAll(RedisCons.KEY_RESOURCE_ROLES_MAP, resourceRolesMap);
        return true;
    }

    /**
     * 刷新用户角色缓存
     * @return
     */
    @Override
    public boolean refreshUserRolesCache(String userType, Long userId){
        // 初始化用户角色
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(userType, userId);
        List<String> roleCodes = BeanUtils.collectToList(roleList, IamRole::getCode);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(V.notEmpty(roleCodes)){
            Collections.sort(roleCodes);
            for(String role : roleCodes){
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        OAuth2Authentication oauth2 = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken token = null;
        if(oauth2.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken oldToken = (UsernamePasswordAuthenticationToken)oauth2.getUserAuthentication();
            LoginUserDetail userDetail = (LoginUserDetail) oldToken.getPrincipal();
            token = new UsernamePasswordAuthenticationToken(userDetail.getUsername(), userDetail.getPassword(), authorities);
            Authentication authentication = new OAuth2Authentication(oauth2.getOAuth2Request(), token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 清空缓存
        String key = userType + ":" + userId;
        redisTemplate.opsForHash().delete(RedisCons.KEY_USER_AUTH_REFRESH_MAP, key);
        return true;
    }

    @Override
    public void addIntoPendingRefresh(String userType, Long userId) {
        String key = userType + ":" + userId;
        redisTemplate.opsForHash().putIfAbsent(RedisCons.KEY_USER_AUTH_REFRESH_MAP, key, true);
    }

}
