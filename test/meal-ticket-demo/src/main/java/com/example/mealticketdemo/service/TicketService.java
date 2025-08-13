package com.example.mealticketdemo.service;

import com.example.mealticketdemo.entity.Ticket;
import com.example.mealticketdemo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Date expireTime) {
        Ticket ticket = new Ticket();
        ticket.setCode(UUID.randomUUID().toString());
        ticket.setExpireTime(expireTime);
        return ticketRepository.save(ticket);
    }

    public Ticket checkTicket(String code) {
        return ticketRepository.findByCode(code);
    }

    public Ticket useTicket(String code) {
        Ticket ticket = ticketRepository.findByCode(code);
        if (ticket != null && ticket.getStatus() == 0 && ticket.getExpireTime().after(new Date())) {
            ticket.setStatus(1);
            return ticketRepository.save(ticket);
        }
        return null;
    }
}