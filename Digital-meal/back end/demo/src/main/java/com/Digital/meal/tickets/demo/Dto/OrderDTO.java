package com.Digital.meal.tickets.demo.Dto;

import java.util.List;

/**
 * 订单数据传输对象
 */
public class OrderDTO {
    
    private String orderId;
    private String orderNumber;
    private String agenciesId;
    private String userId;
    private Integer status;
    private String creator;
    private String createDatetime;
    private String createIP;
    private String suggestion;
    private String expirationTime;
    
    // 关联的订单项
    private List<OrderItemDTO> orderItems;
    
    // 统计信息
    private Integer totalItems;
    private Integer totalQuantity;
    private Double totalValue;

    // 关联信息字段
    private String agenciesName;      // 部门名称
    private String startUserName;     // 发起人姓名
    private String passUserName;      // 审批人姓名
    private Integer orderItemsCount;  // 订单项数量
    private Integer ticketsCount;     // 餐票数量
    
    // 构造函数
    public OrderDTO() {}
    
    public OrderDTO(String orderId, String orderNumber, String agenciesId, String userId, 
                   Integer status, String creator, String createDatetime, String createIP,
                   String suggestion, String expirationTime) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.agenciesId = agenciesId;
        this.userId = userId;
        this.status = status;
        this.creator = creator;
        this.createDatetime = createDatetime;
        this.createIP = createIP;
        this.suggestion = suggestion;
        this.expirationTime = expirationTime;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public String getAgenciesId() { return agenciesId; }
    public void setAgenciesId(String agenciesId) { this.agenciesId = agenciesId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    
    public String getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(String createDatetime) { this.createDatetime = createDatetime; }
    
    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }
    
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    
    public String getExpirationTime() { return expirationTime; }
    public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime; }
    
    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
    
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }

    // 关联信息字段的getter和setter
    public String getAgenciesName() { return agenciesName; }
    public void setAgenciesName(String agenciesName) { this.agenciesName = agenciesName; }

    public String getStartUserName() { return startUserName; }
    public void setStartUserName(String startUserName) { this.startUserName = startUserName; }

    public String getPassUserName() { return passUserName; }
    public void setPassUserName(String passUserName) { this.passUserName = passUserName; }

    public Integer getOrderItemsCount() { return orderItemsCount; }
    public void setOrderItemsCount(Integer orderItemsCount) { this.orderItemsCount = orderItemsCount; }

    public Integer getTicketsCount() { return ticketsCount; }
    public void setTicketsCount(Integer ticketsCount) { this.ticketsCount = ticketsCount; }
}
