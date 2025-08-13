package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;

import com.Digital.meal.tickets.demo.Repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 餐票类型服务 - 提供基本查询功能供订单项关联使用
 */
@Service
public class TicketTypeService {

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    /**
     * 获取所有餐票类型
     */
    public List<TicketTypeEntity> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }

    /**
     * 根据ID获取餐票类型
     */
    public Optional<TicketTypeEntity> getTicketTypeById(String typeId) {
        return ticketTypeRepository.findById(typeId);
    }

    /**
     * 根据食堂ID获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByCanteenId(String canteenId) {
        return ticketTypeRepository.findByCanteenId(canteenId);
    }

    /**
     * 根据状态获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByStatus(Integer status) {
        return ticketTypeRepository.findByStatus(status);
    }

    /**
     * 根据价格范围获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByValueRange(Integer minValue, Integer maxValue) {
        return ticketTypeRepository.findByValueRange(minValue, maxValue);
    }

    /**
     * 检查餐票类型是否存在
     */
    public boolean existsById(String typeId) {
        return ticketTypeRepository.existsById(typeId);
    }

    /**
     * 获取餐票类型总数
     */
    public long count() {
        return ticketTypeRepository.count();
    }
}
