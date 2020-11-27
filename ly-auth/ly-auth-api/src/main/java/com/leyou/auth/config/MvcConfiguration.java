package com.leyou.auth.config;

import com.leyou.auth.interceptors.LoginInterceptor;
import com.leyou.auth.utils.JwtUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class MvcConfiguration implements WebMvcConfigurer {

    private JwtUtils jwtUtils;
    private ClientProperties properties;

    public MvcConfiguration(@Lazy JwtUtils jwtUtils, ClientProperties properties) {
        this.jwtUtils = jwtUtils;
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并得到拦截器注册器
        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor(jwtUtils));
        // 判断用户是否配置了拦截路径，如果没配置走默认，就是拦截 /**
        if(!CollectionUtils.isEmpty(properties.getIncludeFilterPaths())){
            registration.addPathPatterns(properties.getIncludeFilterPaths());
        }
        // 判断用户是否配置了放行路径，如果没配置就是空
        if(!CollectionUtils.isEmpty(properties.getExcludeFilterPaths())){
            registration.excludePathPatterns(properties.getExcludeFilterPaths());
        }
    }
}