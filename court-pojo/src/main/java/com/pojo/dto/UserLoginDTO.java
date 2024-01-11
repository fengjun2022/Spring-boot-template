package com.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
     private  String passWord;


}
