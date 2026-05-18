package com.vibecoding.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vibecoding.product.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT p.*, b.name as brand_name, c.name as category_name " +
            "FROM product p " +
            "LEFT JOIN brand b ON p.brand_id = b.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.deleted = 0 AND p.status = 1 " +
            "ORDER BY p.sort_order DESC, p.id DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectPageList(@Param("offset") int offset, @Param("limit") int limit);
}