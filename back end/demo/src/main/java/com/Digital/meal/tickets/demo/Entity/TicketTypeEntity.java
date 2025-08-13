package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "tickettype")
public class TicketTypeEntity {

    @Id
    @Column(name = "TypeID", length = 64)
    private String typeId;

    @Column(name = "TypeName", nullable = false, length = 10)
    private String typeName;

    @Column(name = "Value", nullable = false)
    private Integer value;

    @Column(name = "CanteenId", nullable = false, length = 64)
    private String canteenId;

    @Column(name = "Status", nullable = false)
    private Integer status;

    @Column(name = "FK_Creator", nullable = false, length = 10)
    private String creator;

    @Column(name = "CreateDatetime", length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", nullable = false, length = 15)
    private String createIP;

    @Column(name = "remark", nullable = false, length = 600)
    private String remark;

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CanteenId", insertable = false, updatable = false)
    @JsonIgnore
    private Canteen canteen;

    @OneToMany(mappedBy = "ticketType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "ticketType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ott> orderItems;

    // 构造函数
    public TicketTypeEntity() {}

    // Getters and Setters
    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public String getCanteenId() { return canteenId; }
    public void setCanteenId(String canteenId) { this.canteenId = canteenId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(String createDatetime) { this.createDatetime = createDatetime; }

    public String getCreateIP() { return createIP; }
    public void setCreateIP(String createIP) { this.createIP = createIP; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
