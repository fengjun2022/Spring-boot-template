package com.ssy.properties;

import com.ssy.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Data
@PropertySource(value = "classpath:security.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "jwt")
@Component
public  class JwtProperties {
    private String secretKey;
    private long ttl;
    private String headName;
    private String headBase;
}
