package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data  //Lombok自动生成getter、setter、toString等方法
@Entity  //标识为JPA实体类
@Table(name="ticket")  //映射数据库表名
public class Ticket {
    @Id  //主键
    @Column(name = "TickerId",length = 64,nullable = false)
    private String tickerId;  // 餐票id

    @Column(name = "Ticketnumber",length = 20)
    private String ticketNumber;

    @Column(name = "TypeID",length = 64,nullable = false)
    private String typeID;  // 餐票类型id

    @Column(name = "StallId",length = 64)
    private String stallId;  //档口Id

    @Column(name = "UsedId",length = 64)
    private String usedId;  //使用者Id

    @Column(name = "CheckId",length = 64)
    private String checkId;  //核销人Id

    @Column(name = "UseTime",length = 10)
    private String useTime;  // 使用时间（可为null）

    @Column(name = "OrderId",length = 64,nullable = false)
    private String orderId;  // 订单id

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TypeID", insertable = false, updatable = false)
    @JsonIgnore
    private TicketTypeEntity ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StallId", insertable = false, updatable = false)
    @JsonIgnore
    private Stall stall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId", insertable = false, updatable = false)
    @JsonIgnore
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsedId", insertable = false, updatable = false)
    @JsonIgnore
    private User usedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CheckId", insertable = false, updatable = false)
    @JsonIgnore
    private User checkUser;

    @Column(name = "ExpirationTime",length = 10,nullable = false)
    private String expirationTime;  // 过期时间

    @Column(name = "CheckTime",length = 10)
    private String checkTime;  // 核销时间

    @Column(name = "Staus",nullable = false)
    private String staus;  // 状态（0-未使用，99-已核销，2-已过期）

    @Column(name = "Fk_Creator",length = 10,nullable = false)
    private String fk_Creator;  //  创建人id

    @Column(name = "CreateDatetime",length = 15,nullable = false)
    private String createDatetime;  //  创建时间

    @Column(name = "CreateIP",length = 15,nullable = false)
    private String createIP;  //  创建IP

    @Column(name = "typename",length = 10,nullable = false)
    private String typename;  // 类型名称


    public String getTickerId() {
        return tickerId;
    }

    public void setTickerId(String tickerId) {
        this.tickerId = tickerId;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getStallId() {
        return stallId;
    }

    public void setStallId(String stallId) {
        this.stallId = stallId;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getUsedId() {
        return usedId;
    }

    public void setUsedId(String usedId) {
        this.usedId = usedId;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getStaus() {
        return staus;
    }

    public void setStaus(String staus) {
        this.staus = staus;
    }

    public String getFk_Creator() {
        return fk_Creator;
    }

    public void setFk_Creator(String fk_Creator) {
        this.fk_Creator = fk_Creator;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getCreateIP() {
        return createIP;
    }

    public void setCreateIP(String createIP) {
        this.createIP = createIP;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

}
