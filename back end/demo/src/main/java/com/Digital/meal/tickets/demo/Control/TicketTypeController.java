package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;
import com.Digital.meal.tickets.demo.Service.TicketTypeService;
import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;

    // 获取所有餐票类型
    @GetMapping
    public Result<List<TicketTypeEntity>> getAllTicketTypes() {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getAllTicketTypes();
        return Result.success("获取餐票类型列表成功", ticketTypes);
    }

    // 根据ID获取餐票类型
    @GetMapping("/{typeId}")
    public ResponseEntity<TicketTypeEntity> getTicketTypeById(@PathVariable String typeId) {
        Optional<TicketTypeEntity> ticketType = ticketTypeService.getTicketTypeById(typeId);
        return ticketType.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 根据食堂ID获取餐票类型
    @GetMapping("/canteen/{canteenId}")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypesByCanteenId(@PathVariable String canteenId) {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getTicketTypesByCanteenId(canteenId);
        return ResponseEntity.ok(ticketTypes);
    }

    // 根据状态获取餐票类型
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypesByStatus(@PathVariable Integer status) {
        List<TicketTypeEntity> ticketTypes = ticketTypeService.getTicketTypesByStatus(status);
        return ResponseEntity.ok(ticketTypes);
    }

    // 检查餐票类型是否存在
    @GetMapping("/{typeId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable String typeId) {
        boolean exists = ticketTypeService.existsById(typeId);
        return ResponseEntity.ok(exists);
    }

    // 获取餐票类型总数
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = ticketTypeService.count();
        return ResponseEntity.ok(count);
    }

    // 添加餐票类型（关联多个食堂）
    @PostMapping
    public Result<TicketTypeEntity> addTicketType(
            @RequestBody TicketTypeEntity ticketType,
            @RequestParam List<String> canteenIds) {

        // 确保关联食堂ID不为空
        if (canteenIds == null || canteenIds.isEmpty()) {
            throw new IllegalArgumentException("至少需要关联一个食堂");
        }

        TicketTypeEntity savedTicketType = ticketTypeService.addTicketType(ticketType, canteenIds);
        return Result.success("添加成功", savedTicketType);
    }

    // 删除餐票类型
    @DeleteMapping("/{typeId}")
    public Result<String> deleteTicketTypeById(@PathVariable String typeId) {
        ticketTypeService.deleteTicketTypeById(typeId);
        return Result.success("餐票类型删除成功");
    }

    // 更新餐票类型（含关联食堂）
    @PutMapping("/{typeId}")
    public Result<TicketTypeEntity> updateTicketType(
            @PathVariable String typeId,
            @RequestBody TicketTypeEntity ticketType,
            @RequestParam(required = false) List<String> canteenIds) {
        TicketTypeEntity updated = ticketTypeService.updateTicketType(typeId, ticketType, canteenIds);
        return Result.success("餐票类型更新成功", updated);
    }

    // 分页获取餐票类型
    @GetMapping("/page")
    public Result<Page<TicketTypeEntity>> getTicketTypesByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        Page<TicketTypeEntity> page = ticketTypeService.getTicketTypesByPage(pageNum, pageSize);
        return Result.success("分页获取餐票类型成功", page);
    }

    // 新增：切换餐票类型状态
    @PostMapping("/{typeId}/toggle-status")
    public Result<String> toggleTicketTypeStatus(
            @PathVariable String typeId,
            @RequestParam Integer status) {
        ticketTypeService.toggleTicketTypeStatus(typeId, status);
        return Result.success("状态切换成功");
    }
}