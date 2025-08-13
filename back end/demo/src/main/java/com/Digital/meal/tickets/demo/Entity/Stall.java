package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "stall")
public class Stall {
    @Id
    @Column(name = "StallId", length = 64)
    private String stallId;

    @Column(name = "StallName", length = 10)
    private String stallName;

    @Column(name = "CanteenId", length = 64)
    private String canteenId;

    @Column(name = "Statue")
    private int statue;

    @Column(name = "FK_Creator", length = 10)
    private String fkCreator;

    @Column(name = "CreateDatetime", length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", length = 15)
    private String createIp;

    @Column(name = "StallLocation", length = 64)
    private String stallLocation;

    @Column(name = "StallNumber", length = 64)
    private String stallNumber;

    @Column(name = "principal", length = 11)
    private String Principal;

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CanteenId", insertable = false, updatable = false)
    @JsonIgnore
    private Canteen canteen;

    @OneToMany(mappedBy = "stall", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets;

    @Column(name = "PrincipalId", length = 64)
    private String principalId;

    // Getter 和 Setter
    public String getStallId() {
        return stallId;
    }

    public void setStallId(String stallId) {
        this.stallId = stallId;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public String getCanteenId() {
        return canteenId;
    }

    public void setCanteenId(String canteenId) {
        this.canteenId = canteenId;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
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

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getStallLocation() {
        return stallLocation;
    }

    public void setStallLocation(String stallLocation) {
        this.stallLocation = stallLocation;
    }

    public String getStallNumber() {
        return stallNumber;
    }

    public void setStallNumber(String stallNumber) {
        this.stallNumber = stallNumber;
    }

    public String getprincipal() {
        return Principal;
    }

    public void setprincipal(String Principal) {
        this.Principal = Principal;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

}
