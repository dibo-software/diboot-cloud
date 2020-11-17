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
package com.diboot.cloud.gateway.config;

import com.diboot.cloud.gateway.auth.GatewayAuthorizationManager;
import com.diboot.cloud.gateway.custom.CustomAccessDeniedHandler;
import com.diboot.cloud.gateway.custom.CustomAuthenticationEntryPoint;
import com.diboot.cloud.gateway.filter.GatewayJwtWebFilter;
import com.diboot.core.config.Cons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 安全过滤器配置
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/15
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityFilterConfig {

    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private GatewayAuthorizationManager authorizationManager;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private GatewayJwtWebFilter gatewayJwtWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 配置url filter
        http.addFilterBefore(gatewayJwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                // 允许匿名的URL
                .pathMatchers(configProperties.getAnonUrlsArray()).permitAll()
                // 需鉴权URL
                .anyExchange().access(authorizationManager)
                .and()
                // 异常处理
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .csrf().disable();

        http.oauth2ResourceServer().authenticationEntryPoint(authenticationEntryPoint)
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthConverter = new JwtGrantedAuthoritiesConverter();
        // 配置鉴权角色的前缀
        grantedAuthConverter.setAuthorityPrefix(Cons.AUTHORITY_PREFIX);
        grantedAuthConverter.setAuthoritiesClaimName(Cons.AUTHORITY_CLAIM_NAME);

        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(grantedAuthConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter);
    }

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

}
