package com.ssy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

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
     private String username;
     private String password;
     private Collection<String> authorities;
     private Integer status;
     private  String token;
}
