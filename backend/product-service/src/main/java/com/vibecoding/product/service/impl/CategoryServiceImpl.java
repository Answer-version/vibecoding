package com.vibecoding.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.product.entity.Category;
import com.vibecoding.product.mapper.CategoryMapper;
import com.vibecoding.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = "category:tree")
    public List<Category> getTree() {
        List<Category> all = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getDeleted, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));

        List<Category> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        for (Category root : roots) {
            buildTree(root, all);
        }

        return roots;
    }

    private void buildTree(Category parent, List<Category> all) {
        List<Category> children = all.stream()
                .filter(c -> parent.getId().equals(c.getParentId()))
                .collect(Collectors.toList());

        for (Category child : children) {
            buildTree(child, all);
        }
    }

    @Override
    public List<Category> list() {
        return categoryMapper.selectList(null);
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }
}
