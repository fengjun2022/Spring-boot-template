package com.court.interceptor;

import com.common.constant.HttpStatus;
import com.common.exception.GlobalException;
import com.common.properties.JwtProperties;
import com.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {


    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预处理逻辑，返回true继续执行处理程序方法，返回false中断请求

        String token = request.getHeader(jwtProperties.getTokenName());
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);

            Claims claims = JwtUtil.parseJWT(token);
            log.info(String.valueOf(claims));
            return true;

        } catch (Exception ex) {
            response.setContentType("text/html;charset=UTF-8");
        if (token==null){
            response.getWriter().write("用户未登录");

        }else {
            response.getWriter().write("登录过期");

        }


           response.setStatus(HttpStatus.NOT_LOGIN);

            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 处理程序方法执行后的后处理逻辑



    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求处理完成后的资源清理等操作

    }

}
