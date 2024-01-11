package com.court.config;

import com.court.interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Component
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor                                                                                                                    ;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
//        定义拦截方法
        registry.addInterceptor(jwtTokenInterceptor)
//                定义拦截路径/case
                .addPathPatterns("/home/**")
                .addPathPatterns("/case/**")
                .addPathPatterns("/user/addUser")
//                定义拦截要排除的
                .excludePathPatterns("/user/login");


    }


    /**
     *
     * @param converters
     */
//    protected  void  extendMessageConverters(List<HttpMessageConverter<?>> converters){
//
//        log.info("扩展消息转换器");
//        MappingJackson2HttpMessageConverter coverter = new MappingJackson2HttpMessageConverter();
//        coverter.setObjectMapper(new JacksonObjectMapper());
//        converters.add(0, coverter);
//
//    }





}
