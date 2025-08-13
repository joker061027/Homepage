package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.Ticket;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository  //标识为数据访问层组件
public interface TicketRepository extends JpaRepository<Ticket,String> {
    // 根据档口ID查询餐票（canteenId对应StallId）
    List<Ticket> findByStallId(String stallId);

    // 查询过期未处理的餐票（状态为0且过期时间小于当前日期）
    List<Ticket> findByExpirationTimeLessThanAndStaus(String expirationTime,String staus);



        // 按食堂和档口分组，同时统计未核销(0)和已核销(1)的餐票数量
        @Query(value = "SELECT " +
                "c.CanteenId AS canteenId, " +
                "c.CanteenName AS canteenName, " +
                "s.StallId AS stallId, " +
                "s.StallNumber AS stallnumber," +
                "s.StallName AS stallName, " +

                // 统计未核销数量（status=0）
                "COUNT(CASE WHEN t.Staus = '0' THEN t.TickerId END) AS unVerifiedCount, " +
                // 统计已核销数量（status=1）
                "COUNT(CASE WHEN t.Staus = '1' THEN t.TickerId END) AS verifiedCount " +
                "FROM ticket t " +
                "LEFT JOIN stall s ON t.StallId = s.StallId " +
                "LEFT JOIN canteen c ON s.CanteenId = c.CanteenId " +
                "GROUP BY c.CanteenId, c.CanteenName, s.StallId, s.StallName " +
                "ORDER BY c.CanteenId, s.StallId",
                nativeQuery = true)
        List<Map<String, Object>> countVerifiedAndUnverifiedByCanteenAndStall();

    // 按代理商分组，汇总状态为0的餐票总数（合并相同agenciesId的数据）
    @Query(value = "SELECT " +
            "a.AgenciesId AS agenciesId, " +
            "a.AgenciesName AS agenciesName, " +
            "SUM(COUNT(t.TickerId)) OVER (PARTITION BY a.AgenciesId) AS unVerifiedCount " +  // 汇总同一代理商的数量
            "FROM ticket t " +
            "LEFT JOIN `order` o ON t.OrderId = o.OrderId " +
            "LEFT JOIN agencies a ON o.AgenciesId = a.AgenciesId " +
            "WHERE t.Staus = '1' " +  // 只统计状态为0的餐票
            "GROUP BY a.AgenciesId, a.AgenciesName " +  // 按代理商ID和名称分组
            "ORDER BY a.AgenciesId",
            nativeQuery = true)
    List<Map<String, Object>> sumUnverifiedTicketsByAgency();


}
