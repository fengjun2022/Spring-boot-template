package com.ssy.mapper;

import com.ssy.annotation.AutoGenerateSnowflakeId;
import com.ssy.dto.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * TODO
 *
 * @author Mr.fengjun
 * @version 1.0
 * @date 2025/3/5
 * @email 3278440884@qq.com
 */

@Mapper
public interface UserMapper {

    @AutoGenerateSnowflakeId
    @Insert("INSERT INTO tem.user (id, username, password, authorities) VALUES (#{id}, #{username}, #{password}, #{authorities})")
    void register(UserEntity user);

    @Select("select id, username, password, authorities,status from tem.user where username = #{username}")
    UserEntity queryUser(String username);
}
