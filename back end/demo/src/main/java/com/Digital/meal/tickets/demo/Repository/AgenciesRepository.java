package com.Digital.meal.tickets.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgenciesRepository extends JpaRepository<Agencies, String> {
    // 继承后自动拥有：save、delete、findById、findAll 等方法
}