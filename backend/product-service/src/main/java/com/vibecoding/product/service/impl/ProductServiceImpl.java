package com.vibecoding.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vibecoding.common.result.PageResult;
import com.vibecoding.product.entity.Product;
import com.vibecoding.product.mapper.ProductMapper;
import com.vibecoding.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public PageResult<Product> pageList(int pageNum, int pageSize, Long categoryId, Long brandId, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0);
        wrapper.eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (brandId != null) {
            wrapper.eq(Product::getBrandId, brandId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getKeywords, keyword);
        }

        wrapper.orderByDesc(Product::getSortOrder);
        IPage<Product> pageResult = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageNum, pageSize);
    }

    @Override
    @Cacheable(value = "product:skus", key = "#productId")
    public List<Object> getSkusByProductId(Long productId) {
        // TODO: 实现获取SKU列表
        return new ArrayList<>();
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public boolean save(Product product) {
        return productMapper.insert(product) > 0;
    }

    @Override
    public boolean updateById(Product product) {
        return productMapper.updateById(product) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return productMapper.deleteById(id) > 0;
    }
}
