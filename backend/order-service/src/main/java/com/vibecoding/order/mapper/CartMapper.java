package com.vibecoding.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vibecoding.order.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}