package com.litian.dancechar.canal;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * canal数据服务启动类
 * @author tojson
 * @date 2021/6/19 21:13
 */
@SpringBootApplication(scanBasePackages = {"com.litian.dancechar"})
@EnableFeignClients(basePackages = "com.litian.dancechar")
@EnableDiscoveryClient
@ConditionalOnNacosDiscoveryEnabled
@Slf4j
public class CanalDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalDataServiceApplication.class, args);
        log.warn("canal数据服务启动成功......");
    }
}