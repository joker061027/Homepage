package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.OrderEntity;
import com.Digital.meal.tickets.demo.Entity.Ott;
import com.Digital.meal.tickets.demo.Entity.OttId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OttRepository extends JpaRepository<Ott, OttId> {

    // 根据订单ID查找所有订单项
    List<Ott> findByOrderId(String orderId);

    // 根据餐票类型ID查找所有订单项
    List<Ott> findByTypeId(String typeId);

    // 使用原生SQL查询
    @Query(value = "SELECT OrderId, TypeID, OredrAmount, Status, FK_Creator, CreateDatetime, CreateIP FROM ott WHERE OrderId = :orderId", nativeQuery = true)
    List<Object[]> findByOrderIdNative(@Param("orderId") String orderId);

    // 获取订单项及餐票类型信息
    @Query(value = "SELECT o.OrderId, o.TypeID, t.TypeName, o.OredrAmount, o.Status, o.FK_Creator, o.CreateDatetime, o.CreateIP, t.Value " +
           "FROM ott o LEFT JOIN tickettype t ON o.TypeID = t.TypeID " +
           "WHERE o.OrderId = :orderId", nativeQuery = true)
    List<Object[]> findOrderItemsWithTypeInfo(@Param("orderId") String orderId);

    // 根据餐票类型获取订单项及相关信息
    @Query(value = "SELECT o.OrderId, o.TypeID, t.TypeName, o.OredrAmount, o.Status, o.FK_Creator, o.CreateDatetime, o.CreateIP, t.Value " +
           "FROM ott o LEFT JOIN tickettype t ON o.TypeID = t.TypeID " +
           "WHERE o.TypeID = :typeId", nativeQuery = true)
    List<Object[]> findOrderItemsByTypeWithInfo(@Param("typeId") String typeId);
    @Query("SELECT o from Ott o join fetch o.order")
    List<Ott> findAll();

    /**
     * 联合查询ott、order和agencies表的所有数据
     */
    @Query(value = "SELECT " +
            "o.OrderId AS orderId, o.OrderNumber AS orderNumber, o.Status AS orderStatus," +
            "ott.TypeID AS typeId, ott.OredrAmount AS orderAmount,ott.CreateDatetime AS CreateDatetime ," +
            "a.AgenciesId AS agenciesId, a.AgenciesName AS agenciesName " +
            "FROM ott " +
            "LEFT JOIN `order` o ON ott.OrderId = o.OrderId " +
            "LEFT JOIN agencies a ON o.AgenciesId = a.AgenciesId " +
            "ORDER BY o.CreateDatetime DESC",
            nativeQuery = true)
    List<Map<String, Object>> findAllJoinedData();


    @Query(value = "SELECT " +
            "o.OrderId AS orderId, o.OrderNumber AS orderNumber, o.Status AS orderStatus," +
            "ott.TypeID AS typeId, ott.OredrAmount AS orderAmount, ott.CreateDatetime AS createDatetime ," +
            "a.AgenciesId AS agenciesId, a.AgenciesName AS agenciesName " +
            "FROM ott " +
            "LEFT JOIN `order` o ON ott.OrderId = o.OrderId " +
            "LEFT JOIN agencies a ON o.AgenciesId = a.AgenciesId " +
            "WHERE o.Status = :status " +  // 状态筛选条件
            "ORDER BY o.CreateDatetime DESC",
            nativeQuery = true)
    List<Map<String, Object>> findAllJoinedDataByStatus(@Param("status") Integer status);



}
