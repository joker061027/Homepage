package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StallRepository extends JpaRepository<Stall, String> {
    List<Stall> findByCanteenId(String canteenId);
}
