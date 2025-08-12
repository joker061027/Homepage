package com.Digital.meal.tickets.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ott")
@IdClass(OttId.class)
public class Ott {

    @Column(name = "OTTID", nullable = false, length = 64)
    private String ottId;

    @Id
    @Column(name = "OrderId", nullable = false, length = 64)
    private String orderId;

    @Id
    @Column(name = "TypeID", nullable = false, length = 64)
    private String typeId;

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId", insertable = false, updatable = false)
    @JsonIgnore
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TypeID", insertable = false, updatable = false)
    @JsonIgnore
    private TicketTypeEntity ticketType;

    @Column(name = "OredrAmount", nullable = false)  // 注意：数据库中字段名拼写为 OredrAmount
    private Integer orderAmount;

    @Column(name = "Status", nullable = false)
    private Integer status;

    @Column(name = "FK_Creator", nullable = true, length = 10)
    private String creator;

    @Column(name = "CreateDatetime", nullable = false, length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", nullable = false, length = 15)
    private String createIP;

    // 构造函数
    public Ott() {}

    public Ott(String ottId, String orderId, String typeId, Integer orderAmount, Integer status,
               String creator, String createDatetime, String createIP) {
        this.ottId = ottId;
        this.orderId = orderId;
        this.typeId = typeId;
        this.orderAmount = orderAmount;
        this.status = status;
        this.creator = creator;
        this.createDatetime = createDatetime;
        this.createIP = createIP;
    }

    // Getters and Setters
    public String getOttId() { return ottId; }
    public void setOttId(String ottId) { this.ottId = ottId; }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }

    public Integer getOrderAmount() { return orderAmount; }
    public void setOrderAmount(Integer orderAmount) { this.orderAmount = orderAmount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(String createDatetime) { this.createDatetime = createDatetime; }

    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }
}
