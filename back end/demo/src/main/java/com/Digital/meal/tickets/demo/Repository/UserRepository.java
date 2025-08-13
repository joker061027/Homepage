package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}