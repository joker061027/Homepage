package com.example.mealticketdemo.repository;

import com.example.mealticketdemo.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    Ticket findByCode(String code);
}