//package com.sky.config;
//
//import com.sky.interceptor.JwtTokenUserInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//import javax.annotation.Resource;
//
///**
// * 配置类，注册web层相关组件
// */
//@Configuration
//@Slf4j
//public class WebMvcConfiguration2 extends WebMvcConfigurationSupport {
//
//    @Resource
//    private JwtTokenUserInterceptor jwtTokenUserInterceptor;
//
//    /**
//     * 注册自定义拦截器
//     * @param registry
//     */
//
//    protected void addInterceptors(InterceptorRegistry registry) {
//        log.info("开始注册自定义拦截器2...");
//        registry.addInterceptor(jwtTokenUserInterceptor)
//                .addPathPatterns("/user/**")
//                .excludePathPatterns("/user/user/login")
//                .excludePathPatterns("/user/shop/status");
//
//
//    }
//
//}
