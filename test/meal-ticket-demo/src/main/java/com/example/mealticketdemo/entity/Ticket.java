package com.example.mealticketdemo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer status = 0; // 0-未使用, 1-已使用

    @Column(name = "expire_time", nullable = false)
    private Date expireTime;

    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime = new Date();
}