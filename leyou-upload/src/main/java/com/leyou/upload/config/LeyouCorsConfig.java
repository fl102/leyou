package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        //添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        //允许的域，不要写* 否则cookie无法使用
        config.addAllowedOrigin("http://manager.leyou.com");
        //是否发送cookie信息
        config.setAllowCredentials(true);
        //允许所有请求方式
        config.addAllowedMethod("*");
        //允许添加任何头信息
        config.addAllowedHeader("*");
        //添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configurationSource);
    }
}
