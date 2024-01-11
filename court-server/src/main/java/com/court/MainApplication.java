package com.court;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// spring的注解驱动事务管理功能
@EnableTransactionManagement //开启注解方式的事务管理
// redis 的缓存配置
//@EnableCaching
//启用配置属性的支持。它允许您将外部配置（通常来自属性文件）绑定和验证到一个Java对象上。
@EnableConfigurationProperties
//启用Spring的调度功能
@EnableScheduling
@SpringBootApplication(scanBasePackages ={"com.court","com.pojo","com.common"})

public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
