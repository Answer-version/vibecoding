package com.vibecoding.cms.controller;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.common.result.R;
import com.vibecoding.cms.entity.CmsArticle;
import com.vibecoding.cms.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cms/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public R<PageResult<CmsArticle>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return R.ok(articleService.pageList(page, pageSize, categoryId));
    }

    @GetMapping("/{id}")
    public R<CmsArticle> get(@PathVariable Long id) {
        CmsArticle article = articleService.getById(id);
        articleService.incrementView(id);
        return R.ok(article);
    }

    @GetMapping("/slug/{slug}")
    public R<CmsArticle> getBySlug(@PathVariable String slug) {
        return R.ok(articleService.getBySlug(slug));
    }

    @GetMapping("/featured")
    public R<List<CmsArticle>> featured() {
        return R.ok(articleService.list());
    }
}