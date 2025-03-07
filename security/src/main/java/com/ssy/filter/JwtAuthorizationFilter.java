package com.ssy.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssy.entity.HttpMessage;
import com.ssy.entity.HttpStatus;
import com.ssy.entity.Result;
import com.ssy.properties.JwtProperties;
import com.ssy.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.auth0.jwt.algorithms.Algorithm;

/**
 * TODO
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/3
 * @email 3278440884@qq.com
 */


public class JwtAuthorizationFilter  extends OncePerRequestFilter {
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    SecurityProperties securityProperties;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        response.setContentType("application/json;charset=UTF-8");

        // 1. 如果在白名单中，直接放行
        if (isPermitAll(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 白名单以外，都要求 Token
        String header = request.getHeader(jwtProperties.getHeadName());
        if (header == null || !header.startsWith(jwtProperties.getHeadBase())) {
            // 没有 Token 或格式错误 => 返回自定义错误
            writeUnauthorizedResponse(response);
            return;
        }

        // 去掉  前缀，获取实际的 Token
        String token = header.replace(jwtProperties.getHeadBase(), "");
        try {
            // 验证 Token 合法性
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()))
                    .build()
                    .verify(token);
            String subject = decodedJWT.getSubject();
            if (subject != null) {

                String authoritiesClaim = decodedJWT.getClaim("authorities").asString();
                Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
                if (authoritiesClaim != null && !authoritiesClaim.isEmpty()) {
                    authorities = Arrays.stream(authoritiesClaim.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                // 将认证信息存入 SecurityContext，后续可以通过 SecurityContextHolder 获取用户信息
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (TokenExpiredException e) {
            // Token 过期
            response.setStatus(HttpStatus.NOT_LOGIN);
            response.getWriter().write(JSON.toJSONString(Result.error(HttpMessage.TOKEN_EXPIRED)));
            return;
        } catch (SignatureVerificationException e) {
            // Token 签名无效，被篡改
            response.setStatus(HttpStatus.NOT_LOGIN);

            response.getWriter().write(JSON.toJSONString(Result.error(HttpMessage.TOKEN_INVALID)));
            return;
        } catch (AlgorithmMismatchException e) {
            // Token 算法不匹配
            response.setStatus(HttpStatus.NOT_LOGIN);
            response.getWriter().write(JSON.toJSONString(Result.error(HttpMessage.TOKEN_ALGORITHM_MISMATCH)));
            return;
        } catch (JWTVerificationException e) {
            // 其他 JWT 验证异常
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.getWriter().write(JSON.toJSONString(Result.error(HttpMessage.BAD_REQUEST+ e.getMessage())));
            return;
        }



        // 继续调用过滤器链中的其他过滤器
        filterChain.doFilter(request, response);
    }


    // 判断请求路径是否在白名单
    private boolean isPermitAll(String requestURI) {
        List<String> permitAllList = securityProperties.getPermitAll();
        // 用 AntPathMatcher 或 startsWith 等进行匹配
        return permitAllList.stream().anyMatch(pattern ->
                new AntPathMatcher().match(pattern, requestURI)
        );
    }

    // 返回 401 响应
    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        Result<Object> error = Result.error(HttpMessage.NO_TOKEN, HttpStatus.NOT_LOGIN);
        response.setStatus(HttpStatus.NOT_LOGIN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(error));

    }

}



