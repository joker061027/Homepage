package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Common.Result;
import com.Digital.meal.tickets.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接测试控制器
 * 用于测试所有表的连接状态
 */
@RestController
@RequestMapping("/database-test")
public class DatabaseTestController {

    @Autowired
    private AgenciesRepository agenciesRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OttRepository ottRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StallRepository stallRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 简单的健康检查
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("数据库测试控制器运行正常");
    }

    /**
     * 测试所有表的连接状态
     */
    @GetMapping("/connection")
    public Result<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试每个表的连接
            long agenciesCount = agenciesRepository.count();
            long canteenCount = canteenRepository.count();
            long orderCount = orderRepository.count();
            long ottCount = ottRepository.count();
            long roleCount = roleRepository.count();
            long stallCount = stallRepository.count();
            long ticketCount = ticketRepository.count();
            long ticketTypeCount = ticketTypeRepository.count();
            long userCount = userRepository.count();

            result.put("status", "success");
            result.put("message", "所有表连接成功");
            result.put("tables", Map.of(
                "agencies", agenciesCount,
                "canteen", canteenCount,
                "order", orderCount,
                "ott", ottCount,
                "role", roleCount,
                "stall", stallCount,
                "ticket", ticketCount,
                "tickettype", ticketTypeCount,
                "user", userCount
            ));

            return Result.success("数据库连接测试成功", result);

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            
            return Result.error("数据库连接测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试单个表的连接
     */
    @GetMapping("/table/{tableName}")
    public Result<Map<String, Object>> testTableConnection(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long count = 0;
            String actualTableName = "";
            
            switch (tableName.toLowerCase()) {
                case "agencies":
                    count = agenciesRepository.count();
                    actualTableName = "agencies";
                    break;
                case "canteen":
                    count = canteenRepository.count();
                    actualTableName = "canteen";
                    break;
                case "order":
                    count = orderRepository.count();
                    actualTableName = "order";
                    break;
                case "ott":
                    count = ottRepository.count();
                    actualTableName = "ott";
                    break;
                case "role":
                    count = roleRepository.count();
                    actualTableName = "role";
                    break;
                case "stall":
                    count = stallRepository.count();
                    actualTableName = "stall";
                    break;
                case "ticket":
                    count = ticketRepository.count();
                    actualTableName = "ticket";
                    break;
                case "tickettype":
                    count = ticketTypeRepository.count();
                    actualTableName = "tickettype";
                    break;
                case "user":
                    count = userRepository.count();
                    actualTableName = "user";
                    break;
                default:
                    return Result.error("未知的表名: " + tableName);
            }

            result.put("tableName", actualTableName);
            result.put("recordCount", count);
            result.put("status", "connected");

            return Result.success("表 " + actualTableName + " 连接成功", result);

        } catch (Exception e) {
            result.put("tableName", tableName);
            result.put("status", "error");
            result.put("error", e.getMessage());
            
            return Result.error("表 " + tableName + " 连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库基本信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("database", "shuzicanpiao1");
            result.put("tables", new String[]{
                "agencies", "canteen", "order", "ott", 
                "role", "stall", "ticket", "tickettype", "user"
            });
            result.put("totalTables", 9);
            result.put("status", "active");

            return Result.success("数据库信息获取成功", result);

        } catch (Exception e) {
            return Result.error("获取数据库信息失败: " + e.getMessage());
        }
    }
}
