package com.Digital.meal.tickets.demo.Dto;

import lombok.Data;

@Data
public class Orderandott {
         // 序号
    private Integer sequence;
    private String orderNumber;
    private Integer status;
    private String createDatetime;
    private Integer totalQuantity;
    private Integer usedQuantity;

    public Orderandott() {
    }

    public Orderandott(String orderNumber, Integer status, String createDatetime, Integer totalQuantity, Integer usedQuantity) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.createDatetime = createDatetime;
        this.totalQuantity = totalQuantity;
        this.usedQuantity = usedQuantity;
    }
     
    /**
     * 获取订单状态的中文描述
     * 
     * @return 返回对应状态码的中文描述：
     *         1-"已批准"，
     *         2-"待审核"，
     *         3-"已拒绝"，
     *         4-"已打印"，
     *         null或其他值-"未知状态"
     */
    public String getStatusDescription() {
        if (status == null) {return "未知";}
        return switch (status) {
            case 1 -> "已批准";
            case 2 -> "待审核";
            case 3 -> "已拒绝";
            case 4 -> "已打印";
            default -> "未知状态";
        };
    }
    public Integer getSequence() {
        return sequence;
    }
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    public String getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getCreateDatetime() {
        return createDatetime;
    }
    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    public Integer getUsedQuantity() {
        return usedQuantity;
    }
    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
}
