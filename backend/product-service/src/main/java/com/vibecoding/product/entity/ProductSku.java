package com.vibecoding.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product_sku")
public class ProductSku {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;
    private String skuCode;
    private String barcode;
    private String name;

    private BigDecimal usdPrice;
    private BigDecimal eurPrice;
    private BigDecimal gbpPrice;
    private BigDecimal cnyPrice;
    private BigDecimal costPrice;
    private BigDecimal usdRetailPrice;
    private BigDecimal usdWholesalePrice;

    private Integer moq;
    private Integer stockQuantity;
    private Integer lowStockAlert;
    private Integer stockStatus;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    private String skuAttrs;
    private String imageUrl;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getUsdPrice() { return usdPrice; }
    public void setUsdPrice(BigDecimal usdPrice) { this.usdPrice = usdPrice; }

    public BigDecimal getEurPrice() { return eurPrice; }
    public void setEurPrice(BigDecimal eurPrice) { this.eurPrice = eurPrice; }

    public BigDecimal getGbpPrice() { return gbpPrice; }
    public void setGbpPrice(BigDecimal gbpPrice) { this.gbpPrice = gbpPrice; }

    public BigDecimal getCnyPrice() { return cnyPrice; }
    public void setCnyPrice(BigDecimal cnyPrice) { this.cnyPrice = cnyPrice; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getUsdRetailPrice() { return usdRetailPrice; }
    public void setUsdRetailPrice(BigDecimal usdRetailPrice) { this.usdRetailPrice = usdRetailPrice; }

    public BigDecimal getUsdWholesalePrice() { return usdWholesalePrice; }
    public void setUsdWholesalePrice(BigDecimal usdWholesalePrice) { this.usdWholesalePrice = usdWholesalePrice; }

    public Integer getMoq() { return moq; }
    public void setMoq(Integer moq) { this.moq = moq; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getLowStockAlert() { return lowStockAlert; }
    public void setLowStockAlert(Integer lowStockAlert) { this.lowStockAlert = lowStockAlert; }

    public Integer getStockStatus() { return stockStatus; }
    public void setStockStatus(Integer stockStatus) { this.stockStatus = stockStatus; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getWidth() { return width; }
    public void setWidth(BigDecimal width) { this.width = width; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public String getSkuAttrs() { return skuAttrs; }
    public void setSkuAttrs(String skuAttrs) { this.skuAttrs = skuAttrs; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
