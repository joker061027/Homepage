package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}