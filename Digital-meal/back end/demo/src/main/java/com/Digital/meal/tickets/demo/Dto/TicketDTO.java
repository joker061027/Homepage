package com.Digital.meal.tickets.demo.Dto;

import lombok.Data;

import java.util.List;

/**
 * 餐票数据传输对象(DTO)
 * 用于控制器与客户端之间的数据交互，避免直接暴露实体类
 */
@Data
public class TicketDTO {
    private String ticketId;
    private String typeID;
    private String stallId;
    private String usedId;
    private String checkId;
    private String useTime;
    private String orderId;
    private String expirationTime;
    private String checkTime;
    private String staus;
    private String creator;
    private String createDatetime;
    private String createIp;
    private String CanteenName; // 新增食堂名称字段
    private String CanteenId;

    public void setTickerId(String substring) {
        this.ticketId = substring;
    }

    public void setCanteenId(String canteenId) {
        this.CanteenId=canteenId;
    }

    /**
     * 批量核销餐票专用内部类
     * 替代原BatchCancelDto
     */
    @Data  // 补充lombok注解，自动生成getter/setter
    public static class BatchCancel {
        private List<String> ticketId;
        private String checkId;
    }

}
