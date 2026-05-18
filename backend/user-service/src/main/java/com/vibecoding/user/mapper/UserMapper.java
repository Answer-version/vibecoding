package com.vibecoding.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vibecoding.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}