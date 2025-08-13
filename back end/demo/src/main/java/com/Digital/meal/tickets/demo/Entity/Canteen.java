package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "canteen")
public class Canteen {

    @Id
    @Column(name = "CanteenId", length = 64)
    private String canteenId;

    @Column(name = "CanteenName", length = 10)
    private String canteenName;

    @Column(name = "Status")
    private int status;

    @Column(name = "FK_Creator", length = 10)
    private String fkCreator;

    @Column(name = "CreateDatetime", length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", length = 15)
    private String createIp;

    // JPA关联关系
    @OneToMany(mappedBy = "canteen", fetch = FetchType.LAZY)
    private List<Stall> stalls;

    @OneToMany(mappedBy = "canteen", fetch = FetchType.LAZY)
    private List<TicketTypeEntity> ticketTypes;

    // Getter 和 Setter
    public String getCanteenId() {
        return canteenId;
    }

    public void setCanteenId(String canteenId) {
        this.canteenId = canteenId;
    }

    public String getCanteenName() {
        return canteenName;
    }

    public void setCanteenName(String canteenName) {
        this.canteenName = canteenName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFkCreator() {
        return fkCreator;
    }

    public void setFkCreator(String fkCreator) {
        this.fkCreator = fkCreator;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIP(String createIp) {
        this.createIp = createIp;
    }
}
