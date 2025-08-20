package com.pojo.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/3
 * @email 3278440884@qq.com
 */
@Data
public class UserEntity implements Serializable {

     private long id;
     @NotNull(message="账号名不能为空")
     private String username;
     @NotNull(message="密码不能为空")
     private String password;
     private Collection<String> authorities;
}
