package com.Digital.meal.tickets.demo.Dto;

import java.math.BigDecimal;

/**
 * 订单项与订单信息的DTO
 * 用于返回订单项及其关联的订单信息
 */
public class OrderItemWithOrderInfoDTO {
    
    // 订单项信息
    private Long orderItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String ticketTypeName;
    private BigDecimal ticketTypePrice;
    
    // 订单信息
    private Long orderId;
    private String orderNumber;
    private String orderStatus;
    private String orderCreateTime;
    private String customerName;
    private String customerPhone;
    private BigDecimal orderTotalAmount;
    
    // 构造函数
    public OrderItemWithOrderInfoDTO() {}
    
    public OrderItemWithOrderInfoDTO(Long orderItemId, Integer quantity, BigDecimal unitPrice, 
                                   BigDecimal totalPrice, String ticketTypeName, BigDecimal ticketTypePrice,
                                   Long orderId, String orderNumber, String orderStatus, 
                                   String orderCreateTime, String customerName, String customerPhone,
                                   BigDecimal orderTotalAmount) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.ticketTypeName = ticketTypeName;
        this.ticketTypePrice = ticketTypePrice;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderCreateTime = orderCreateTime;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderTotalAmount = orderTotalAmount;
    }
    
    // Getters and Setters
    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getTicketTypeName() { return ticketTypeName; }
    public void setTicketTypeName(String ticketTypeName) { this.ticketTypeName = ticketTypeName; }
    
    public BigDecimal getTicketTypePrice() { return ticketTypePrice; }
    public void setTicketTypePrice(BigDecimal ticketTypePrice) { this.ticketTypePrice = ticketTypePrice; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    
    public String getOrderCreateTime() { return orderCreateTime; }
    public void setOrderCreateTime(String orderCreateTime) { this.orderCreateTime = orderCreateTime; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public BigDecimal getOrderTotalAmount() { return orderTotalAmount; }
    public void setOrderTotalAmount(BigDecimal orderTotalAmount) { this.orderTotalAmount = orderTotalAmount; }
}
