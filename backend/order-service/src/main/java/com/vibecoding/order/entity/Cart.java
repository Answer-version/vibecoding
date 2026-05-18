package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer cartType;
    private Long userId;
    private String sessionId;
    private Integer itemCount;
    private BigDecimal usdAmount;
    private BigDecimal eurAmount;
    private BigDecimal gbpAmount;
    private BigDecimal cnyAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCartType() { return cartType; }
    public void setCartType(Integer cartType) { this.cartType = cartType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
    public BigDecimal getUsdAmount() { return usdAmount; }
    public void setUsdAmount(BigDecimal usdAmount) { this.usdAmount = usdAmount; }
    public BigDecimal getEurAmount() { return eurAmount; }
    public void setEurAmount(BigDecimal eurAmount) { this.eurAmount = eurAmount; }
    public BigDecimal getGbpAmount() { return gbpAmount; }
    public void setGbpAmount(BigDecimal gbpAmount) { this.gbpAmount = gbpAmount; }
    public BigDecimal getCnyAmount() { return cnyAmount; }
    public void setCnyAmount(BigDecimal cnyAmount) { this.cnyAmount = cnyAmount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}