package com.diboot.file.config;

import com.diboot.core.util.D;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.S;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.service.impl.FastdfsAbstractFileStorageServiceImpl;
import com.diboot.file.service.impl.LocalFileStorageServiceImpl;
import com.diboot.file.stater.FileProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
public class FileSpringConfig  implements WebMvcConfigurer {

    /**
     * 覆盖Jackson转换
     **/
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        // Long转换成String避免JS超长问题
        SimpleModule simpleModule = new SimpleModule();

        // 不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);
        // 时间格式化
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat(D.FORMAT_DATETIME_Y4MDHMS));
        // 设置格式化内容
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
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
