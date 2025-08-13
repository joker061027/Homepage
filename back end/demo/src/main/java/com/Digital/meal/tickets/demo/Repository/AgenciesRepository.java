package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.Agencies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenciesRepository extends JpaRepository<Agencies, String> {
    // 继承后自动拥有：save、delete、findById、findAll 等方法
}