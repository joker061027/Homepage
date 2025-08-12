package com.Digital.meal.tickets.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "RoleId")
    private String roleId;

    @Column(name = "RoleName")
    private String roleName;

    // JPA关联关系
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @Column(name = "Status")
    private Integer status;

    @Column(name = "FK_Creator")
    private String fkCreator;

    @Column(name = "CreateDateIP")
    private String createDateIP;

    @Column(name = "CreateIP")
    private String createIP;
}