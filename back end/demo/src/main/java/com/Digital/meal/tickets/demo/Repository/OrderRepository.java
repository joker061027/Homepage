package com.Digital.meal.tickets.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Digital.meal.tickets.demo.Entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    // 根据订单号查找订单
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    // 根据发起用户ID查找订单
    List<OrderEntity> findByStartUserId(String startUserId);

    // 根据机构ID查找订单
    List<OrderEntity> findByAgenciesId(String agenciesId);

    // 根据状态查找订单
    List<OrderEntity> findByStatus(Integer status);

    // 根据发起用户ID和状态查找订单
    List<OrderEntity> findByStartUserIdAndStatus(String startUserId, Integer status);

    // 查找即将过期的订单
    @Query(value = "SELECT * FROM `order` WHERE ExpirationTime <= :expirationDate AND Status = 1", nativeQuery = true)
    List<OrderEntity> findExpiringOrders(@Param("expirationDate") String expirationDate);

    // 统计发起用户订单数量
    @Query(value = "SELECT COUNT(*) FROM `order` WHERE StartUserid = :startUserId", nativeQuery = true)
    Long countByStartUserId(@Param("startUserId") String startUserId);

    // 统计机构订单数量
    @Query(value = "SELECT COUNT(*) FROM `order` WHERE AgenciesId = :agenciesId", nativeQuery = true)
    Long countByAgenciesId(@Param("agenciesId") String agenciesId);

    // ========== 分页查询方法 ==========

    // 分页查询所有订单（JpaRepository已经提供了findAll(Pageable)方法）
    // Page<OrderEntity> findAll(Pageable pageable); // 这个方法已经继承了，不需要重复定义

    // 根据发起用户ID分页查询订单
    Page<OrderEntity> findByStartUserId(String startUserId, Pageable pageable);

    // 根据机构ID分页查询订单
    Page<OrderEntity> findByAgenciesId(String agenciesId, Pageable pageable);

    // 根据状态分页查询订单
    Page<OrderEntity> findByStatus(Integer status, Pageable pageable);

    // 根据发起用户ID和状态分页查询订单
    Page<OrderEntity> findByStartUserIdAndStatus(String startUserId, Integer status, Pageable pageable);

    // 自定义分页查询 - 根据创建时间范围
    @Query(value = "SELECT * FROM `order` WHERE CreateDatetime BETWEEN :startDate AND :endDate ORDER BY CreateDatetime DESC",
           countQuery = "SELECT COUNT(*) FROM `order` WHERE CreateDatetime BETWEEN :startDate AND :endDate",
           nativeQuery = true)
    Page<OrderEntity> findByCreateDatetimeBetween(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate,
                                                  Pageable pageable);
                                                  
                                                 

    // ========== 首页餐票订单相关查询 ==========

    /**
     * 查询首页餐票订单汇总信息
     */
    @Query(nativeQuery = true, value =
        "SELECT " +
        "  o.OrderNumber AS orderNumber, " +
        "  o.CreateDatetime AS createDatetime, " +
        "  COALESCE(SUM(ott.OredrAmount), 0) AS totalOrderAmount, " +
        "  COALESCE(COUNT(CASE WHEN t.Staus = '1' THEN 1 END), 0) AS ticketStatus1Count, " +
        "  o.Status AS orderStatus " +
        "FROM `order` o " +
        "LEFT JOIN ott ON o.OrderId = ott.OrderId " +
        "LEFT JOIN ticket t ON o.OrderId = t.OrderId " +
        "GROUP BY o.OrderNumber, o.CreateDatetime, o.Status " +
        "ORDER BY o.CreateDatetime DESC")
    List<Object[]> findOrderSummaries();

    /**
     * 根据订单ID列表查询订单汇总信息 - 用于分页
     */
    @Query(nativeQuery = true, value =
        "SELECT " +
        "  o.OrderNumber AS orderNumber, " +
        "  o.CreateDatetime AS createDatetime, " +
        "  COALESCE(SUM(ott.OredrAmount), 0) AS totalOrderAmount, " +
        "  COALESCE(COUNT(CASE WHEN t.Staus = '1' THEN 1 END), 0) AS ticketStatus1Count, " +
        "  o.Status AS orderStatus " +
        "FROM `order` o " +
        "LEFT JOIN ott ON o.OrderId = ott.OrderId " +
        "LEFT JOIN ticket t ON o.OrderId = t.OrderId " +
        "WHERE o.OrderId IN :orderIds " +
        "GROUP BY o.OrderNumber, o.CreateDatetime, o.Status " +
        "ORDER BY o.CreateDatetime DESC")
    List<Object[]> findOrderSummariesByOrderIds(@Param("orderIds") List<String> orderIds);

    
}