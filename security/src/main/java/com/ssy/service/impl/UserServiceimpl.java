package com.ssy.service.impl;

import com.ssy.dto.UserEntity;
import com.ssy.mapper.UserMapper;
import com.ssy.service.UserService;
import com.ssy.utils.SecurePasswordEncryptorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * TODO
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/5
 * @email 3278440884@qq.com
 */

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    SecurePasswordEncryptorUtil securePasswordEncryptorUtil;
    @Autowired
    UserMapper userMapper;
    @Override
    public void register(UserEntity user) {
        CompletableFuture<String> stringCompletableFuture = securePasswordEncryptorUtil.encryptPassword(user.getPassword());
        String password = stringCompletableFuture.join();
        user.setPassword(password);
        userMapper.register(user);
    }
}
