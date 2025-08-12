package com.Digital.meal.tickets.demo.Dto;

/**
 * 订单项数据传输对象
 */
public class OrderItemDTO {
    
    private String orderId;
    private String typeId;
    private String typeName;
    private Integer orderAmount;
    private Integer unitPrice;
    private Integer totalPrice;
    private Integer status;
    private String creator;
    private String createDatetime;
    private String createIP;
    
    // 构造函数
    public OrderItemDTO() {}
    
    public OrderItemDTO(String orderId, String typeId, String typeName, Integer orderAmount, 
                       Integer unitPrice, Integer totalPrice, Integer status, 
                       String creator, String createDatetime, String createIP) {
        this.orderId = orderId;
        this.typeId = typeId;
        this.typeName = typeName;
        this.orderAmount = orderAmount;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.creator = creator;
        this.createDatetime = createDatetime;
        this.createIP = createIP;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    public Integer getOrderAmount() { return orderAmount; }
    public void setOrderAmount(Integer orderAmount) { this.orderAmount = orderAmount; }
    
    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
    
    public Integer getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    
    public String getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(String createDatetime) { this.createDatetime = createDatetime; }
    
    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }
}
