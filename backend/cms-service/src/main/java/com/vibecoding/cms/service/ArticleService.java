package com.vibecoding.cms.service;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.cms.entity.CmsArticle;

import java.util.List;

public interface ArticleService {

    List<CmsArticle> list();

    PageResult<CmsArticle> pageList(int pageNum, int pageSize, Long categoryId);

    CmsArticle getById(Long id);

    CmsArticle getBySlug(String slug);

    boolean save(CmsArticle article);

    boolean update(CmsArticle article);

    boolean delete(Long id);

    void incrementView(Long id);
}