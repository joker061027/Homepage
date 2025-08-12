package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "`order`")
public class OrderEntity {

    @Id
    @Column(name = "OrderId", length = 64)
    private String orderId;

    @Column(name = "OrderNumber", nullable = false, length = 10)
    private String orderNumber;

    @Column(name = "AgenciesId", nullable = false, length = 64)
    private String agenciesId;

    @Column(name = "PassUserId", nullable = false, length = 64)
    private String passUserId;

    @Column(name = "Status", nullable = false)
    private Integer status;

    @Column(name = "FK_Creator", nullable = false, length = 10)
    private String fkCreator;

    @Column(name = "CreateDatetime", nullable = false, length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", nullable = false, length = 15)
    private String createIP;

    @Column(name = "Suggestion", nullable = false, length = 300)
    private String suggestion;

    @Column(name = "ExpirationTime", nullable = false, length = 15)
    private String expirationTime;

    @Column(name = "PassTime", nullable = false, length = 10)
    private String passTime;

    @Column(name = "StartUserid", nullable = false, length = 64)
    private String startUserId;

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AgenciesId", insertable = false, updatable = false)
    @JsonIgnore
    private Agencies agencies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StartUserid", insertable = false, updatable = false)
    @JsonIgnore
    private User startUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PassUserId", insertable = false, updatable = false)
    @JsonIgnore
    private User passUser;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ott> orderItems;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets;

    // 构造函数
    public OrderEntity() {}

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getAgenciesId() { return agenciesId; }
    public void setAgenciesId(String agenciesId) { this.agenciesId = agenciesId; }

    public String getPassUserId() { return passUserId; }
    public void setPassUserId(String passUserId) { this.passUserId = passUserId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getFkCreator() { return fkCreator; }
    public void setFkCreator(String fkCreator) { this.fkCreator = fkCreator; }

    public String getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(String createDatetime) { this.createDatetime = createDatetime; }

    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }

    // 暂时注释掉，因为字段被注释了
    // public String getCreator() { return creator; }
    // public void setCreator(String creator) { this.creator = creator; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getExpirationTime() { return expirationTime; }
    public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime; }

    public String getPassTime() { return passTime; }
    public void setPassTime(String passTime) { this.passTime = passTime; }

    public String getStartUserId() { return startUserId; }
    public void setStartUserId(String startUserId) { this.startUserId = startUserId; }

    // 为了向后兼容，保留userId的getter和setter方法，映射到startUserId
    public String getUserId() { return startUserId; }
    public void setUserId(String userId) { this.startUserId = userId; }

    // 关联对象的getter和setter方法
    public Agencies getAgencies() { return agencies; }
    public void setAgencies(Agencies agencies) { this.agencies = agencies; }

    public User getStartUser() { return startUser; }
    public void setStartUser(User startUser) { this.startUser = startUser; }

    public User getPassUser() { return passUser; }
    public void setPassUser(User passUser) { this.passUser = passUser; }

    public List<Ott> getOrderItems() { return orderItems; }
    public void setOrderItems(List<Ott> orderItems) { this.orderItems = orderItems; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
}
