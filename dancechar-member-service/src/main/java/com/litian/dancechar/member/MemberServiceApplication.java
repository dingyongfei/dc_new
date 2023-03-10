package com.litian.dancechar.member;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 会员积分服务启动类
 * @author tojson
 * @date 2021/6/19 21:15
 */
@SpringBootApplication(scanBasePackages = {"com.litian.dancechar"})
@EnableFeignClients(basePackages = "com.litian.dancechar")
@EnableDiscoveryClient
@ConditionalOnNacosDiscoveryEnabled
@Slf4j
public class MemberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
        log.warn("积分服务启动成功......");
    }
}