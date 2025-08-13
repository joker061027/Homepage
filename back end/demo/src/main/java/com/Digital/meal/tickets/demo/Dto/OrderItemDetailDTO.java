package com.Digital.meal.tickets.demo.Dto;

import lombok.Data;

/**
 * 订单项详情DTO - 用于表示订单中的每个餐票类型项
 * 主要用于TicketManagement页面的OrderDetailModal弹窗显示
 */
@Data
public class OrderItemDetailDTO {
    private Integer sequence;        // 序号
    private String typeName;         // 餐票类型名称
    private Integer orderAmount;     // 订购数量
    
    public OrderItemDetailDTO() {
    }
    
    public OrderItemDetailDTO(Integer sequence, String typeName, Integer orderAmount) {
        this.sequence = sequence;
        this.typeName = typeName;
        this.orderAmount = orderAmount;
    }
} 