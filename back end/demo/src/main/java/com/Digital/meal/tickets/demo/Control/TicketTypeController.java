package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;
import com.Digital.meal.tickets.demo.Service.TicketTypeService;
import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 餐票类型控制器 - 提供基本查询接口供订单项关联使用
 */
@RestController
@RequestMapping("/ticket-types")

public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;



    /**
     * 获取所有餐票类型
     */
    @GetMapping
    public Result<List<TicketTypeEntity>> getAllTicketTypes() {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getAllTicketTypes();
        return Result.success("获取餐票类型列表成功", ticketTypes);
    }

    /**
     * 根据ID获取餐票类型
     */
    @GetMapping("/{typeId}")
    public ResponseEntity<TicketTypeEntity> getTicketTypeById(@PathVariable String typeId) {
        Optional<TicketTypeEntity> ticketType = ticketTypeService.getTicketTypeById(typeId);
        return ticketType.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据食堂ID获取餐票类型
     */
    @GetMapping("/canteen/{canteenId}")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypesByCanteenId(@PathVariable String canteenId) {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getTicketTypesByCanteenId(canteenId);
        return ResponseEntity.ok(ticketTypes);
    }

    /**
     * 根据状态获取餐票类型
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypesByStatus(@PathVariable Integer status) {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getTicketTypesByStatus(status);
        return ResponseEntity.ok(ticketTypes);
    }

    /**
     * 根据价格范围获取餐票类型
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypesByValueRange(
            @RequestParam Integer minValue, 
            @RequestParam Integer maxValue) {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getTicketTypesByValueRange(minValue, maxValue);
        return ResponseEntity.ok(ticketTypes);
    }

    /**
     * 检查餐票类型是否存在
     */
    @GetMapping("/{typeId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable String typeId) {
        boolean exists = ticketTypeService.existsById(typeId);
        return ResponseEntity.ok(exists);
    }

    /**
     * 获取餐票类型总数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = ticketTypeService.count();
        return ResponseEntity.ok(count);
    }
}
