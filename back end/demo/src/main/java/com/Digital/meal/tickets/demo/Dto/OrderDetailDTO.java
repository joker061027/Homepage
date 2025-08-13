package com.Digital.meal.tickets.demo.Dto;

import java.util.List;

import lombok.Data;

/**
 * 订单详情DTO - 用于返回订单的详细信息
 * 主要用于TicketManagement页面的OrderDetailModal弹窗显示
 */
@Data
public class OrderDetailDTO {
    // 订单基本信息
    private String orderId;
    private String orderNumber;
    private String agenciesName;  // 申请单位
    private String applicantName; // 申请人
    private Integer status;
    private String statusDescription;
    private String createDatetime;
    private String suggestion;
    private String expirationTime;
    private String passTime;
    
    // 订单项列表
    private List<OrderItemDetailDTO> orderItems;
    
    public OrderDetailDTO() {
    }
    
    /**
     * 获取订单状态的中文描述
     */
    public String getStatusDescription() {
        if (status == null) {
            statusDescription = "未知";
            return statusDescription;
        }
        statusDescription = switch (status) {
            case 1 -> "审核通过";
            case 0 -> "待审核";
            case -1 -> "已拒绝";
            case 99 -> "已撤销";
            default -> "未知状态";
        };
        return statusDescription;
    }
} 