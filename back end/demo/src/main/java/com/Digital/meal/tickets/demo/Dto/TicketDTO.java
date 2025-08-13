package com.Digital.meal.tickets.demo.Dto;

import lombok.Data;

import java.util.List;

/**
 * 餐票数据传输对象(DTO)
 * 用于控制器与客户端之间的数据交互，避免直接暴露实体类
 */
@Data
public class TicketDTO {
    private String tickerId;
    private String ticketNumber;
    private String typeID;
    private String typename;
    private String stallId;
    private String usedId;
    private String checkId;
    private String useTime;
    private String orderId;
    private String expirationTime;
    private String checkTime;
    private String staus;
    private String fk_Creator;
    private String createDatetime;
    private String createIP;
    private String canteenName; // 新增食堂名称字段
    private String canteenId;

    /**
     * 批量核销餐票专用内部类
     * 替代原BatchCancelDto
     */
    @Data  // 补充lombok注解，自动生成getter/setter
    public static class BatchCancel {
        private List<String> ticketId;
        private String checkId;
    }

    // 在TicketDTO类中添加（BatchCancel类下方）
    @Data
    public static class VerifyTicket {
        private String checkId; // 核销人（后勤人员ID，前端传递当前登录用户）
    }

    /**
     * 批量核销餐票的请求参数
     */
    @Data
    public static class BatchVerify {
        private List<String> ticketIds; // 待核销的餐票ID列表
        private String checkId;        // 核销人ID（当前登录后勤人员）
    }

    @Data
    public static class CreateTicketRequest {
        private String orderNumber; // 订单编号
        private Integer quantity;   // 餐票数量
        private String expirationTime; // 过期时间
        private String fkCreator;   // 创建者
        private String status = "0"; // 默认为未使用状态
    }
}
