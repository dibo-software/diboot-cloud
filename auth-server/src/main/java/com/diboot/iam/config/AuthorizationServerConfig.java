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

import com.diboot.iam.entity.LoginUserDetail;
import com.diboot.iam.cons.IAMConfig;
import com.diboot.iam.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务器配置
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private int accessTokenExpiresIn = 300, refreshTokenExpiresIn = 7*24*3600;

    /**
     * 资源服务
     */
    private String[] resourceIds = {"auth-server", "scheduler", "file-server", "message-server","example-api"};

    /*
    * 数据库管理ClientDetailsService
    @Autowired
    @Qualifier("iamClientDetailsService")
    private ClientDetailsService clientService;

    @Bean("iamClientDetailsService")
    public ClientDetailsService clientDetailsService(DataSource dataSource, PasswordEncoder passwordEncoder) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }*/

    /**
     * 内存管理ClientDetailsService
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //clients.withClientDetails(clientService);
        // 设置客户端认证信息
        clients.inMemory()
                .withClient("pc").secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .resourceIds(resourceIds)
                .scopes("read", "write")
                .accessTokenValiditySeconds(accessTokenExpiresIn).refreshTokenValiditySeconds(refreshTokenExpiresIn)
                .and()
                .withClient("mobile").secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .resourceIds(resourceIds)
                .scopes("read", "write")
                .accessTokenValiditySeconds(accessTokenExpiresIn).refreshTokenValiditySeconds(refreshTokenExpiresIn);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        JwtAccessTokenConverter jwtTokenConverter = jwtAccessTokenConverter();
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenConverter);
        delegates.add(tokenEnhancer());
        enhancerChain.setTokenEnhancers(delegates);
        endpoints.tokenStore(redisTokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService) // 加载用户信息的Service
                .accessTokenConverter(jwtTokenConverter)
                .tokenEnhancer(enhancerChain)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST)
                .reuseRefreshTokens(false); //refresh_token 不可重复使用
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtTokenConverter = new JwtAccessTokenConverter();
        jwtTokenConverter.setKeyPair(keyPair());
        return jwtTokenConverter;
    }

    /**
     * 从jwt.jks证书获取密钥对
     * @return
     */
    @Bean
    public KeyPair keyPair() {
        char[] pwdChars = IAMConfig.JKS_PASSWORD.toCharArray();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), pwdChars);
        return keyStoreKeyFactory.getKeyPair("jwt", pwdChars);
    }

    /**
     * redis token 配置
     */
    @Bean("redisTokenStore")
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * 扩展Token，添加更多属性值
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer(){
        return (accessToken, authentication) -> {
            LoginUserDetail loginUserDetail = (LoginUserDetail) authentication.getPrincipal();
            // 扩展字段
            Map<String, Object> extInfo = new HashMap(){{
                put("userType", loginUserDetail.getUserType());
                put("userId", loginUserDetail.getUserId());
            }};
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(extInfo);
            return accessToken;
        };
    }
}
