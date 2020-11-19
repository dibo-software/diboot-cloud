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
package com.diboot.cloud.gateway.auth;

import com.diboot.cloud.redis.RedisCons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/14
 */
@Slf4j
@Component
public class GatewayAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        //从Redis中获取当前路径可访问角色列表
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        Map<Object, Object> resourceRolesMap = redisTemplate.opsForHash().entries(RedisCons.KEY_RESOURCE_ROLES_MAP);
        // 请求URI
        String requestUri = request.getMethodValue().toUpperCase() + ":" + request.getURI().getPath();
        // 先匹配固定URI
        List<String> matchRoles = null;
        if(resourceRolesMap.containsKey(requestUri)){
            matchRoles = (List<String>)resourceRolesMap.get(requestUri);
        }
        else{
            // 前缀
            for(Map.Entry<Object, Object> entry : resourceRolesMap.entrySet()){
                if(PATH_MATCHER.match((String)entry.getKey(), requestUri)){
                    matchRoles = (List<String>)entry.getValue();
                    break;
                }
            }
        }
        // 无须检查权限的url，忽略
        if(matchRoles == null){
            log.debug("忽略无权限约束的URL: {}", requestUri);
            return Mono.just(new AuthorizationDecision(true));
        }
        log.debug("检查权限：URL: {}, 可访问Roles: {}", requestUri, matchRoles);
        //当前URL允许角色范围内的用户可访问
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(matchRoles::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
