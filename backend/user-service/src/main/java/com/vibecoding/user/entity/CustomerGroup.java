package com.vibecoding.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("customer_group")
public class CustomerGroup {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer groupType;
    private String groupCode;
    private String groupName;
    private String groupNameEn;
    private BigDecimal discountRate;
    private Integer moq;
    private String description;
    private Integer sortOrder;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getGroupType() { return groupType; }
    public void setGroupType(Integer groupType) { this.groupType = groupType; }
    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getGroupNameEn() { return groupNameEn; }
    public void setGroupNameEn(String groupNameEn) { this.groupNameEn = groupNameEn; }
    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    public Integer getMoq() { return moq; }
    public void setMoq(Integer moq) { this.moq = moq; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}