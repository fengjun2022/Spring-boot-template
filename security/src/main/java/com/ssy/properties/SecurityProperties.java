package com.ssy.properties;
import com.ssy.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.List;
/**
 *
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/4
 * @email 3278440884@qq.com
 */


@Data
@Component
@PropertySource(value = "classpath:security.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "security")

public class SecurityProperties {

    private List<String> permitAll;
    private List<RoleMapping> roleBased;

    public void setPermitAll(List<String> permitAll) {
        this.permitAll = permitAll;
    }

    public void setRoleBased(List<RoleMapping> roleBased) {
        this.roleBased = roleBased;
    }

    public static class RoleMapping {
        private String pattern;
        private String role;  // 例如：ADMIN

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}