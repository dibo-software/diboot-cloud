package com.diboot.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * file启动类
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/13  17:41
 */
@EnableFeignClients({"com.diboot.cloud.service"})
@SpringBootApplication
@EnableDiscoveryClient
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
