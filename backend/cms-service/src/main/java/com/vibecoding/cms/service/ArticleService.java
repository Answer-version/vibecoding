package com.vibecoding.cms.service;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.cms.entity.CmsArticle;

import java.util.List;

public interface ArticleService {

    PageResult<CmsArticle> pageList(int pageNum, int pageSize, Long categoryId);

    CmsArticle getById(Long id);

    CmsArticle getBySlug(String slug);

    CmsArticle save(CmsArticle article);

    void update(CmsArticle article);

    void delete(Long id);

    void incrementView(Long id);
}