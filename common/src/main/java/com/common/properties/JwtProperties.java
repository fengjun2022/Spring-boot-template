package com.common.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public  class JwtProperties {
    private String SecretKey;
    private long Ttl;
    private String TokenName;
}
