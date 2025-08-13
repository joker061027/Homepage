package com.Digital.meal.tickets.demo.Dto;


import lombok.Data;

@Data
public class CreateTicketRequest {
    private String orderNumber;
    private Integer orderAmount;
}