package com.vibecoding.product.service;

import com.vibecoding.product.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getTree();

    List<Category> list();

    Category getById(Long id);
}
