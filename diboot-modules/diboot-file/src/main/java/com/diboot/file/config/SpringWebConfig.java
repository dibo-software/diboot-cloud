package com.diboot.file.config;

import com.diboot.core.util.DateConverter;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.S;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.service.impl.LocalFileStorageServiceImpl;
import com.diboot.file.stater.FileProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

/**
 * FileSpringConfig配置
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/21  15:00
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String file = S.join("file:", PropertiesUtils.get("files.storage.directory"), File.separator);
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/",
                        "classpath:/resources/", "classpath:/static/", "classpath:/public/", file);
    }

    /**
     * 使用本地存储
     *
     * @return
     */
    @Bean
    public FileStorageService fileStorageService() {
        return new LocalFileStorageServiceImpl();
    }

}
