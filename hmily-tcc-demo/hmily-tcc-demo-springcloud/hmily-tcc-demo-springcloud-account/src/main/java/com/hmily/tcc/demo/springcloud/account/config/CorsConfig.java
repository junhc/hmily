package com.hmily.tcc.demo.springcloud.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Name: CorsConfig
 * Function:
 *
 * @Author: K.K
 * Create Time: 2025/6/14 17:24
 * Modified By:
 * Modified Time:
 * Description:
 * Version:
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有接口
                .allowedOrigins("*")  // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许方法
                .allowedHeaders("*")  // 允许所有头
                .allowCredentials(true)  // 允许携带cookie
                .maxAge(3600);  // 预检请求缓存时间
    }
}
