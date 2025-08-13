package com.Digital.meal.tickets.demo.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 草稿订单数据传输对象
 * 用于在缓存中存储临时订单数据
 */
public class DraftOrderDTO {
    
    private String draftId;              // 草稿ID（缓存键）
    private String userId;               // 用户ID
    private String agenciesId;           // 机构ID
    private String expirationTime;       // 过期时间
    private List<DraftOrderItemDTO> orderItems; // 订单项列表
    
    // 草稿元数据
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;    // 草稿创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;    // 草稿更新时间
    private String createIP;             // 创建IP
    private Integer status;              // 草稿状态：0-编辑中，1-待提交，2-已提交
    
    // 构造函数
    public DraftOrderDTO() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = 0; // 默认编辑中状态
    }
    
    public DraftOrderDTO(String userId, String agenciesId) {
        this();
        this.userId = userId;
        this.agenciesId = agenciesId;
        this.draftId = generateDraftId(userId);
    }
    
    /**
     * 生成草稿ID
     */
    private String generateDraftId(String userId) {
        return "DRAFT_" + userId + "_" + System.currentTimeMillis();
    }
    
    /**
     * 更新草稿时间戳
     */
    public void updateTimestamp() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 计算订单项总数量
     */
    @JsonIgnore
    public Integer getTotalQuantity() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(item -> item.getAmount() != null ? item.getAmount() : 0)
                .sum();
    }
    
    /**
     * 计算订单项总种类数
     */
    @JsonIgnore
    public Integer getTotalItemTypes() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.size();
    }
    
    /**
     * 检查草稿是否为空
     */
    @JsonIgnore
    public boolean isEmpty() {
        return orderItems == null || orderItems.isEmpty() || 
               orderItems.stream().allMatch(item -> item.getAmount() == null || item.getAmount() <= 0);
    }
    
    /**
     * 验证草稿数据是否有效
     */
    @JsonIgnore
    public boolean isValid() {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        if (agenciesId == null || agenciesId.trim().isEmpty()) {
            return false;
        }
        if (isEmpty()) {
            return false;
        }
        // 验证所有订单项都有效
        return orderItems.stream().allMatch(item -> 
            item.getTypeId() != null && !item.getTypeId().trim().isEmpty() &&
            item.getAmount() != null && item.getAmount() > 0
        );
    }
    
    // Getters and Setters
    public String getDraftId() { return draftId; }
    public void setDraftId(String draftId) { this.draftId = draftId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getAgenciesId() { return agenciesId; }
    public void setAgenciesId(String agenciesId) { this.agenciesId = agenciesId; }
    
    public String getExpirationTime() { return expirationTime; }
    public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime; }
    
    public List<DraftOrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<DraftOrderItemDTO> orderItems) { this.orderItems = orderItems; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    @Override
    public String toString() {
        return "DraftOrderDTO{" +
                "draftId='" + draftId + '\'' +
                ", userId='" + userId + '\'' +
                ", agenciesId='" + agenciesId + '\'' +
                ", status=" + status +
                ", totalItems=" + getTotalItemTypes() +
                ", totalQuantity=" + getTotalQuantity() +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
