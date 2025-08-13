package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "UserId", length = 64)
    private String userId;

    @Column(name = "UserName", length = 20)
    private String userName;

    @Column(name = "RoleId", length = 64)
    private String roleId;

    @Column(name = "AgenciesId", length = 64)
    private String agenciesId;

    // JPA关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AgenciesId", insertable = false, updatable = false)
    @JsonIgnore
    private Agencies agencies;

    @OneToMany(mappedBy = "startUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderEntity> startedOrders;

    @OneToMany(mappedBy = "passUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderEntity> approvedOrders;

    @OneToMany(mappedBy = "usedUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> usedTickets;

    @OneToMany(mappedBy = "checkUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> checkedTickets;

    @Column(name = "FullName", length = 10)
    private String fullName;

    @Column(name = "Password", length = 20)
    private String password;

    @Column(name = "Status")
    private Integer status;

    @Column(name = "FK_Creator", length = 10)
    private String fkCreator;

    @Column(name = "CreateDatetime", length = 15)
    private String createDatetime;

    @Column(name = "CreateIP", length = 15)
    private String createIP;

    @Column(name = "phonenumber", length = 11)
    private String phoneNumber;
}