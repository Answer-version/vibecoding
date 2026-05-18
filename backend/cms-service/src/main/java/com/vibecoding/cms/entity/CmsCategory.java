package com.vibecoding.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_category")
public class CmsCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String categoryName;
    private String categoryNameEn;
    private String slug;
    private String description;
    private Integer sortOrder;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}