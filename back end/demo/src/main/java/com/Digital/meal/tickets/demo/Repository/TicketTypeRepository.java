package com.Digital.meal.tickets.demo.Repository;

import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, String> {

    // 根据餐票类型名称查找
    List<TicketTypeEntity> findByTypeName(String typeName);

    // 根据食堂ID查找餐票类型
    List<TicketTypeEntity> findByCanteenId(String canteenId);

    // 根据状态查找餐票类型
    List<TicketTypeEntity> findByStatus(Integer status);

    // 根据价格范围查找餐票类型
    @Query(value = "SELECT * FROM tickettype WHERE Value BETWEEN :minValue AND :maxValue", nativeQuery = true)
    List<TicketTypeEntity> findByValueRange(@Param("minValue") Integer minValue, @Param("maxValue") Integer maxValue);

}
