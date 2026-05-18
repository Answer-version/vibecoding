package com.vibecoding.product.controller;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.common.result.R;
import com.vibecoding.product.entity.Product;
import com.vibecoding.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public R<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String keyword) {
        PageResult<Product> result = productService.pageList(page, pageSize, categoryId, brandId, keyword);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<Product> get(@PathVariable Long id) {
        return R.ok(productService.getById(id));
    }

    @GetMapping("/{id}/skus")
    public R<List<Object>> getSkus(@PathVariable Long id) {
        return R.ok(productService.getSkusByProductId(id));
    }
}