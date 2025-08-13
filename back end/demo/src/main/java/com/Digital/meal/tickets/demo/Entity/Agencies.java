package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Data
@Entity
@Table(name = "agencies")
public class Agencies {
    @Id
    @Column(name = "AgenciesId", length = 64)    // 映射表字段 AgenciesId
    private String agenciesId;

    @Column(name = "AgenciesName", length = 20)  // 映射表字段 AgenciesName
    private String agenciesName;

    @Column(name = "Status")        // 映射表字段 Status
    private Integer status;

    @Column(name = "FK_Creator", length = 10)    // 映射表字段 FK_Creator
    private String fkCreator;

    @Column(name = "CreateDatetime", length = 15)// 映射表字段 CreateDatetime
    private String createDatetime;

    @Column(name = "CreateIP", length = 15)      // 映射表字段 CreateIP
    private String createIP;

    // JPA关联关系
    @OneToMany(mappedBy = "agencies", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "agencies", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderEntity> orders;
}