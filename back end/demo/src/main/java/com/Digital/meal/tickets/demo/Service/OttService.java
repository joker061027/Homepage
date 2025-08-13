package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Dto.OrderItemDTO;
import com.Digital.meal.tickets.demo.Entity.Ott;
import com.Digital.meal.tickets.demo.Repository.OttRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OttService {

    @Autowired
    private OttRepository ottRepository;

    /**
     * 创建订单项
     */
    public Ott createOrderItem(String orderId, String typeId, Integer amount) {
        Ott ott = new Ott();

        // 设置主键
        ott.setOttId(generateOttId());

        // 设置订单和类型ID
        ott.setOrderId(orderId);
        ott.setTypeId(typeId);

        // 设置订单数量
        ott.setOrderAmount(amount);

        // 设置审计字段
        ott.setStatus(1); // 默认状态
        ott.setCreator("SYSTEM"); // 临时设置，实际应从当前用户获取
        ott.setCreateDatetime(getCurrentDateTime());
        ott.setCreateIP("127.0.0.1"); // 临时设置，实际应从请求获取

        return ottRepository.save(ott);
    }

    /**
     * 根据订单ID获取订单项基本信息
     */
    public List<Ott> getOrderItemsByOrderId(String orderId) {
        return ottRepository.findByOrderId(orderId);
    }

    /**
     * 根据订单ID获取订单项及订单信息
     */
    public List<Ott> getOrderItemsWithOrder(String orderId) {
        return ottRepository.findByOrderId(orderId);
    }

    /**
     * 根据订单ID获取完整信息（包含餐票类型）
     */
    public List<Ott> getOrderItemsWithFullInfo(String orderId) {
        return ottRepository.findByOrderId(orderId);
    }

    /**
     * 根据订单ID获取统计信息
     */
    public Map<String, Object> getOrderStatistics(String orderId) {
        List<Ott> orderItems = ottRepository.findByOrderId(orderId);

        Map<String, Object> statistics = new HashMap<>();

        if (orderItems.isEmpty()) {
            statistics.put("totalItems", 0);
            statistics.put("totalQuantity", 0);
            statistics.put("totalValue", 0);
            statistics.put("itemTypes", 0);
            return statistics;
        }

        int totalItems = orderItems.size();
        int totalQuantity = 0;

        for (Ott item : orderItems) {
            totalQuantity += item.getOrderAmount();
        }

        statistics.put("orderId", orderId);
        statistics.put("totalItems", totalItems);
        statistics.put("totalQuantity", totalQuantity);
        statistics.put("itemTypes", totalItems);

        return statistics;
    }

    /**
     * 使用原生SQL查询测试
     */
    public List<Object[]> getOrderItemsNative(String orderId) {
        return ottRepository.findByOrderIdNative(orderId);
    }

    /**
     * 根据订单ID获取订单项详细信息（包含餐票类型名称和价值）
     */
    public List<OrderItemDTO> getOrderItemsWithTypeInfo(String orderId) {
        List<Object[]> results = ottRepository.findOrderItemsWithTypeInfo(orderId);
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (Object[] row : results) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setOrderId((String) row[0]);           // OrderId
            dto.setTypeId((String) row[1]);            // TypeID
            dto.setTypeName((String) row[2]);          // TypeName
            dto.setOrderAmount((Integer) row[3]);      // OredrAmount
            dto.setStatus((Integer) row[4]);           // Status
            dto.setCreator((String) row[5]);           // FK_Creator
            dto.setCreateDatetime((String) row[6]);    // CreateDatetime
            dto.setCreateIP((String) row[7]);          // CreateIP

            Integer value = (Integer) row[8];          // Value
            dto.setUnitPrice(value);
            dto.setTotalPrice(value * dto.getOrderAmount());

            orderItems.add(dto);
        }

        return orderItems;
    }

    /**
     * 根据餐票类型ID获取订单项详细信息
     */
    public List<OrderItemDTO> getOrderItemsByTypeWithInfo(String typeId) {
        List<Object[]> results = ottRepository.findOrderItemsByTypeWithInfo(typeId);
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (Object[] row : results) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setOrderId((String) row[0]);           // OrderId
            dto.setTypeId((String) row[1]);            // TypeID
            dto.setTypeName((String) row[2]);          // TypeName
            dto.setOrderAmount((Integer) row[3]);      // OredrAmount
            dto.setStatus((Integer) row[4]);           // Status
            dto.setCreator((String) row[5]);           // FK_Creator
            dto.setCreateDatetime((String) row[6]);    // CreateDatetime
            dto.setCreateIP((String) row[7]);          // CreateIP

            Integer value = (Integer) row[8];          // Value
            dto.setUnitPrice(value);
            dto.setTotalPrice(value * dto.getOrderAmount());

            orderItems.add(dto);
        }

        return orderItems;
    }

    /**
     * 生成订单项ID
     */
    private String generateOttId() {
        return "OTT" + System.currentTimeMillis();
    }

    /**
     * 获取当前日期时间
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 获取ott、order和agencies表的所有联合数据
     */
    public List<Map<String, Object>> getAllJoinedData() {
        return ottRepository.findAllJoinedData();
    }

    /**
     * 根据状态查询ott、order和agencies表的联合数据
     */
    public List<Map<String, Object>> getJoinedDataByStatus(Integer status) {
        return ottRepository.findAllJoinedDataByStatus(status);
    }




}
