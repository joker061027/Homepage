package com.Digital.meal.tickets.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 简单的数据库连接测试
 */
public class DatabaseConnectionTest {
    
    private static final String URL = "jdbc:mysql://localhost:3306/shuzicanpiao1?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    public static void main(String[] args) {
        System.out.println("开始测试数据库连接...");
        
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL驱动加载成功");
            
            // 建立连接
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ 数据库连接成功！");
            
            // 测试查询
            Statement statement = connection.createStatement();
            
            // 检查数据库中的表
            String[] tables = {"agencies", "canteen", "order", "ott", "role", "stall", "ticket", "tickettype", "user"};
            
            System.out.println("\n📊 检查数据库表：");
            for (String tableName : tables) {
                try {
                    ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM " + tableName);
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("  ✅ " + tableName + " 表存在，记录数: " + count);
                    }
                    rs.close();
                } catch (Exception e) {
                    System.out.println("  ❌ " + tableName + " 表检查失败: " + e.getMessage());
                }
            }
            
            // 关闭连接
            statement.close();
            connection.close();
            System.out.println("\n✅ 数据库连接测试完成！");
            
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
