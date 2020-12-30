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
package com.diboot.iam.config;

import com.diboot.iam.handler.CustomAccessDeniedHandler;
import com.diboot.iam.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 资源服务器相关配置
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${spring.application.name}")
    private String resourceId = "auth-server";

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private TokenStore redisTokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(authenticationEntryPoint)
                .tokenStore(redisTokenStore)
                .accessDeniedHandler(customAccessDeniedHandler);
        resources.resourceId(resourceId).stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().antMatchers("/oauth/**").permitAll()
            .and()
            .authorizeRequests().anyRequest().authenticated();
    }

}
