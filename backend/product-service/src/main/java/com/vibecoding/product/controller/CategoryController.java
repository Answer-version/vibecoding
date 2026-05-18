package com.vibecoding.product.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.product.entity.Category;
import com.vibecoding.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public R<List<Category>> getTree() {
        return R.ok(categoryService.getTree());
    }

    @GetMapping
    public R<List<Category>> list() {
        return R.ok(categoryService.list());
    }

    @GetMapping("/{id}")
    public R<Category> get(@PathVariable Long id) {
        return R.ok(categoryService.getById(id));
    }
}