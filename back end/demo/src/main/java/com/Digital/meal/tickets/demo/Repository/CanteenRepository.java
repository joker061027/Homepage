package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanteenRepository extends JpaRepository<Canteen, String> {
}


