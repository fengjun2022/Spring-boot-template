package com.common.utils;





import com.common.properties.JwtProperties;
import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

import java.util.UUID;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;
    private static JwtProperties jwtPT;


    // jti：jwt的唯一身份标识
    public static final String JWT_ID = UUID.randomUUID().toString();

    @PostConstruct
    public void INIT (){
        jwtPT = jwtProperties;
    }


    // 由字符串生成加密key
    public static SecretKey generalKey() {
        // 本地的密码解码

        byte[] encodedKey = BaseEncoding.base64().decode(jwtPT.getSecretKey());
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    public static String createJWT(long ttMillis, Map<String, Object> claims) {


        // 指定名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成jwt时间
        long expMillis = System.currentTimeMillis() + ttMillis;
        Date exp = new Date(expMillis);

        SecretKey key = generalKey();
        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key)
                .setExpiration(exp);
        return builder.compact();
    }


    //     解密jwt
    public static Claims parseJWT(String jwt) throws Exception {


        SecretKey key = generalKey(); // 签名秘钥，和生成的签名的秘钥一模一样
        Claims claims = Jwts.parser() // 得到DefaultJwtParser
                .setSigningKey(key) // 设置签名的秘钥
                .parseClaimsJws(jwt).getBody(); // 设置需要解析的jwt
        return claims;
    }


}
