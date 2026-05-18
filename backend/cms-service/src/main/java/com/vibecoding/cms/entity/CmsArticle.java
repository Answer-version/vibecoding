package com.vibecoding.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_article")
public class CmsArticle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private String articleTitle;
    private String articleTitleEn;
    private String slug;
    private String summary;
    private String summaryEn;
    private String content;
    private String contentEn;
    private String coverImage;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private Integer viewCount;
    private Integer sortOrder;
    private Integer isFeatured;
    private Integer status;
    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}