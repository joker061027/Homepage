package com.example.mealticketdemo.controller;

import com.example.mealticketdemo.entity.Ticket;
import com.example.mealticketdemo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    public Map<String, Object> createTicket(@RequestParam(defaultValue = "30") int expireMinutes) {
        Date expireTime = new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000);
        Ticket ticket = ticketService.createTicket(expireTime);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", ticket);
        return result;
    }

    @GetMapping("/check")
    public Map<String, Object> checkTicket(@RequestParam String code) {
        Ticket ticket = ticketService.checkTicket(code);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);

        Map<String, Object> data = new HashMap<>();
        if (ticket == null) {
            data.put("valid", false);
            data.put("message", "餐票不存在");
        } else if (ticket.getStatus() == 1) {
            data.put("valid", false);
            data.put("message", "餐票已使用");
        } else if (ticket.getExpireTime().before(new Date())) {
            data.put("valid", false);
            data.put("message", "餐票已过期");
        } else {
            data.put("valid", true);
            data.put("message", "餐票有效");
            data.put("ticket", ticket);
        }

        result.put("data", data);
        return result;
    }

    @PostMapping("/use")
    public Map<String, Object> useTicket(@RequestParam String code) {
        Ticket ticket = ticketService.useTicket(code);

        Map<String, Object> result = new HashMap<>();
        if (ticket != null) {
            result.put("code", 200);
            result.put("message", "餐票使用成功");
            result.put("data", ticket);
        } else {
            result.put("code", 400);
            result.put("message", "餐票使用失败，可能已使用或过期");
        }
        return result;
    }
}