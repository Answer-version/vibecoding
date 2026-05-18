package com.vibecoding.product.service;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.product.entity.Product;

import java.util.List;

public interface ProductService {

    PageResult<Product> pageList(int page, int pageSize, Long categoryId, Long brandId, String keyword);

    List<Object> getSkusByProductId(Long productId);

    Product getById(Long id);

    boolean save(Product product);

    boolean updateById(Product product);

    boolean removeById(Long id);
}
