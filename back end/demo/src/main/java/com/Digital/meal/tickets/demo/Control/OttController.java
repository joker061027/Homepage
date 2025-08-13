package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Dto.OrderItemDTO;
import com.Digital.meal.tickets.demo.Entity.Ott;
import com.Digital.meal.tickets.demo.Service.OttService;
import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ott")

public class OttController {

    @Autowired
    private OttService ottService;


    /**
     * 根据订单ID获取订单项基本信息
     */
    @GetMapping("/order/{orderId}")
    public Result<List<OrderItemDTO>> getOrderItems(@PathVariable String orderId) {
        List<OrderItemDTO> orderItems = ottService.getOrderItemsWithTypeInfo(orderId);
        return Result.success("获取订单项成功", orderItems);
    }

    /**
     * 根据订单ID获取订单项基本信息（原始Ott对象）
     */
    @GetMapping("/order/{orderId}/basic")
    public ResponseEntity<List<Ott>> getOrderItemsBasic(@PathVariable String orderId) {
        List<Ott> orderItems = ottService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * 根据订单ID获取订单项及订单信息
     */
    @GetMapping("/order/{orderId}/with-order")
    public ResponseEntity<List<Ott>> getOrderItemsWithOrder(@PathVariable String orderId) {
        List<Ott> orderItems = ottService.getOrderItemsWithOrder(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * 根据订单ID获取完整信息（包含餐票类型）
     */
    @GetMapping("/order/{orderId}/full-info")
    public ResponseEntity<List<Ott>> getOrderItemsWithFullInfo(@PathVariable String orderId) {
        List<Ott> orderItems = ottService.getOrderItemsWithFullInfo(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * 根据订单ID获取统计信息
     */
    @GetMapping("/order/{orderId}/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics(@PathVariable String orderId) {
        Map<String, Object> statistics = ottService.getOrderStatistics(orderId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 根据餐票类型ID获取订单项详细信息
     */
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsByType(@PathVariable String typeId) {
        List<OrderItemDTO> orderItems = ottService.getOrderItemsByTypeWithInfo(typeId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * 测试原生SQL查询
     */
    @GetMapping("/order/{orderId}/native")
    public ResponseEntity<List<Object[]>> getOrderItemsNative(@PathVariable String orderId) {
        List<Object[]> orderItems = ottService.getOrderItemsNative(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * 联合查询ott、order和agencies表的所有数据
     * 自动通过外键关联匹配
     */
    @GetMapping("/joined/all")
    public Result<List<Map<String, Object>>> getAllJoinedData() {
        List<Map<String, Object>> result = ottService.getAllJoinedData();
        return Result.success("联合查询成功", result);
    }

    /**
     * 按状态联合查询ott、order和agencies表的所有数据
     */
    @GetMapping("/joined/by-status")
    public Result<List<Map<String, Object>>> getJoinedDataByStatus(@RequestParam Integer status) {
        List<Map<String, Object>> result = ottService.getJoinedDataByStatus(status);
        return Result.success("按状态联合查询成功", result);
    }
}