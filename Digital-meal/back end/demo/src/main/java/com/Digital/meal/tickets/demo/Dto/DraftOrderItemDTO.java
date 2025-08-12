package com.Digital.meal.tickets.demo.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 草稿订单项数据传输对象
 */
public class DraftOrderItemDTO {
    
    private String typeId;      // 餐票类型ID
    private Integer amount;     // 数量
    private String typeName;    // 餐票类型名称（可选，用于显示）
    private Double unitPrice;   // 单价（可选，用于计算）
    private String description; // 描述（可选）
    
    // 构造函数
    public DraftOrderItemDTO() {}
    
    public DraftOrderItemDTO(String typeId, Integer amount) {
        this.typeId = typeId;
        this.amount = amount;
    }
    
    public DraftOrderItemDTO(String typeId, Integer amount, String typeName) {
        this.typeId = typeId;
        this.amount = amount;
        this.typeName = typeName;
    }
    
    /**
     * 计算总价（如果有单价的话）
     */
    @JsonIgnore
    public Double getTotalPrice() {
        if (unitPrice != null && amount != null) {
            return unitPrice * amount;
        }
        return null;
    }
    
    /**
     * 验证订单项是否有效
     */
    @JsonIgnore
    public boolean isValid() {
        return typeId != null && !typeId.trim().isEmpty() && 
               amount != null && amount > 0;
    }
    
    /**
     * 检查是否为空订单项
     */
    @JsonIgnore
    public boolean isEmpty() {
        return amount == null || amount <= 0;
    }
    
    // Getters and Setters
    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "DraftOrderItemDTO{" +
                "typeId='" + typeId + '\'' +
                ", amount=" + amount +
                ", typeName='" + typeName + '\'' +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DraftOrderItemDTO that = (DraftOrderItemDTO) o;
        return typeId != null ? typeId.equals(that.typeId) : that.typeId == null;
    }
    
    @Override
    public int hashCode() {
        return typeId != null ? typeId.hashCode() : 0;
    }
}
