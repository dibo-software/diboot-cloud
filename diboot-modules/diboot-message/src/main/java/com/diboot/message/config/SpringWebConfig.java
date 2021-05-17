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
package com.diboot.message.config;

import com.diboot.core.util.DateConverter;
import com.diboot.message.channel.ChannelStrategy;
import com.diboot.message.channel.SimpleEmailChannel;
import com.diboot.message.service.TemplateVariableService;
import com.diboot.message.service.impl.SystemTemplateVariableServiceImpl;
import com.diboot.message.starter.MessageProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * FileSpringConfig配置
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  15:00
 */
@Configuration
@EnableConfigurationProperties(MessageProperties.class)
@ComponentScan(basePackages = {"com.diboot.**"})
@MapperScan(basePackages = {"com.diboot.**.mapper"})
public class SpringWebConfig implements WebMvcConfigurer {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    /**
     * 覆盖Jackson转换
     **/
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, jacksonMessageConverter);
    }

    /**
     * 默认支持String-Date类型转换
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateConverter());
    }

    /**
     * 模版变量服务
     *
     * @return
     */
    @Bean
    public TemplateVariableService templateVariableService() {
        return new SystemTemplateVariableServiceImpl();
    }

    /**
     * 简单邮箱发送通道
     *
     * @return
     */
    @Bean("EMAIL")
    public ChannelStrategy simpleEmailChannel() {
        return new SimpleEmailChannel();
    }

}
