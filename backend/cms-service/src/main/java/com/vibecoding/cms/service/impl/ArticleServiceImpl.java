package com.vibecoding.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vibecoding.cms.entity.CmsArticle;
import com.vibecoding.cms.mapper.CmsArticleMapper;
import com.vibecoding.cms.service.ArticleService;
import com.vibecoding.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements ArticleService {

    @Override
    @Cacheable(value = "cms:articles", key = "#pageNum + ':' + #pageSize")
    public PageResult<CmsArticle> pageList(int pageNum, int pageSize, Long categoryId) {
        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticle::getDeleted, 0);
        wrapper.eq(CmsArticle::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(CmsArticle::getCategoryId, categoryId);
        }

        wrapper.orderByDesc(CmsArticle::getPublishTime);
        IPage<CmsArticle> page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize);
    }

    @Override
    @Cacheable(value = "cms:article", key = "#id")
    public CmsArticle getById(Long id) {
        return getById(id);
    }

    @Override
    @Cacheable(value = "cms:article:slug", key = "#slug")
    public CmsArticle getBySlug(String slug) {
        return getOne(new LambdaQueryWrapper<CmsArticle>()
                .eq(CmsArticle::getSlug, slug)
                .eq(CmsArticle::getDeleted, 0)
                .eq(CmsArticle::getStatus, 1));
    }

    @Override
    @CacheEvict(value = {"cms:articles", "cms:article", "cms:article:slug"}, allEntries = true)
    public CmsArticle save(CmsArticle article) {
        if (article.getId() == null) {
            article.setCreateTime(LocalDateTime.now());
            article.setViewCount(0);
        }
        article.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(article);
        return article;
    }

    @Override
    @CacheEvict(value = {"cms:articles", "cms:article", "cms:article:slug"}, allEntries = true)
    public void update(CmsArticle article) {
        article.setUpdateTime(LocalDateTime.now());
        updateById(article);
    }

    @Override
    @CacheEvict(value = {"cms:articles", "cms:article", "cms:article:slug"}, allEntries = true)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public void incrementView(Long id) {
        CmsArticle article = getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            updateById(article);
        }
    }
}