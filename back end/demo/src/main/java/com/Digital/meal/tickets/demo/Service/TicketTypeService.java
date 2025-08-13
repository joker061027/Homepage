package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Entity.Canteen;
import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;


import com.Digital.meal.tickets.demo.Repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketTypeService {

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    /**
     * 获取所有餐票类型
     */
    public List<TicketTypeEntity> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }

    /**
     * 根据ID获取餐票类型
     */
    public Optional<TicketTypeEntity> getTicketTypeById(String typeId) {
        return ticketTypeRepository.findById(typeId);
    }

    /**
     * 根据食堂ID获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByCanteenId(String canteenId) {
        return ticketTypeRepository.findByCanteenId(canteenId);
    }

    /**
     * 根据状态获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByStatus(Integer status) {
        return ticketTypeRepository.findByStatus(status);
    }

    /**
     * 根据价格范围获取餐票类型
     */
    public List<TicketTypeEntity> getTicketTypesByValueRange(Integer minValue, Integer maxValue) {
        return ticketTypeRepository.findByValueRange(minValue, maxValue);
    }

    /**
     * 检查餐票类型是否存在
     */
    public boolean existsById(String typeId) {
        return ticketTypeRepository.existsById(typeId);
    }

    // 获取餐票类型总数
    public long count() {
        return ticketTypeRepository.count();
    }

    // 添加餐票类型（关联多个食堂）
    public TicketTypeEntity addTicketType(TicketTypeEntity ticketType, List<String> canteenIds) {
        // 校验必填字段
        if (ticketType.getTypeName() == null || ticketType.getTypeName().trim().isEmpty()) {
            throw new IllegalArgumentException("餐票名称不能为空");
        }
        if (canteenIds == null || canteenIds.isEmpty()) {
            throw new IllegalArgumentException("至少需要关联一个食堂");
        }
        if (ticketType.getValue() == null || ticketType.getValue() <= 0) {
            throw new IllegalArgumentException("面额必须为正数");
        }

        // 生成TypeID
        String newTypeId;
        Optional<TicketTypeEntity> lastTicket = ticketTypeRepository.findTopByOrderByTypeIdDesc();
        if (lastTicket.isEmpty()) {
            newTypeId = "type_001";
        } else {
            String lastId = lastTicket.get().getTypeId();
            String numStr = lastId.substring(5);
            int nextNum = Integer.parseInt(numStr) + 1;
            newTypeId = "type_" + String.format("%03d", nextNum);
        }
        ticketType.setTypeId(newTypeId);

        // 自动填充默认值
        ticketType.setStatus(1);
        ticketType.setCreator("system");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        ticketType.setCreateDatetime(now);
        ticketType.setCreateIP("127.0.0.1");
        ticketType.setRemark("全天使用");

        // 先保存餐票类型（不包含关联）
        TicketTypeEntity savedTicketType = ticketTypeRepository.save(ticketType);

        // 获取关联的食堂列表
        List<Canteen> canteens = canteenRepository.findAllById(canteenIds);
        if (canteens.size() != canteenIds.size()) {
            List<String> existingIds = canteens.stream().map(Canteen::getCanteenId).collect(Collectors.toList());
            String missingIds = canteenIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.joining(","));
            throw new IllegalArgumentException("食堂ID不存在：" + missingIds);
        }

        // 通过维护方(Canteen)添加关系
        for (Canteen canteen : canteens) {
            // 初始化ticketTypes集合
            if (canteen.getTicketTypes() == null) {
                canteen.setTicketTypes(new ArrayList<>());
            }
            // 添加当前餐票类型到食堂
            if (!canteen.getTicketTypes().contains(savedTicketType)) {
                canteen.getTicketTypes().add(savedTicketType);
            }
            // 保存食堂（维护方保存会更新中间表）
            canteenRepository.save(canteen);
        }

        return savedTicketType;
    }

    // 删除餐票类型
    public void deleteTicketTypeById(String typeId) {
        TicketTypeEntity ticketType = ticketTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("餐票类型ID不存在"));

        // 从所有关联食堂中移除
        if (ticketType.getCanteens() != null) {
            for (Canteen canteen : ticketType.getCanteens()) {
                if (canteen.getTicketTypes() != null) {
                    canteen.getTicketTypes().remove(ticketType);
                    canteenRepository.save(canteen);
                }
            }
        }

        ticketTypeRepository.delete(ticketType);
    }

    // 更新餐票类型（含关联食堂）
    public TicketTypeEntity updateTicketType(String typeId, TicketTypeEntity ticketType, List<String> canteenIds) {
        TicketTypeEntity existing = ticketTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("餐票类型ID不存在"));

        // 校验并更新基本信息
        if (ticketType.getTypeName() == null || ticketType.getTypeName().trim().isEmpty()) {
            throw new IllegalArgumentException("餐票名称不能为空");
        }
        if (ticketType.getValue() == null || ticketType.getValue() <= 0) {
            throw new IllegalArgumentException("面额必须为正数");
        }
        existing.setTypeName(ticketType.getTypeName());
        existing.setValue(ticketType.getValue());
        existing.setOvertime(ticketType.getOvertime());

        // 更新关联食堂（如果传入了新列表）
        if (canteenIds != null) {
            // 获取现有关联的食堂
            List<Canteen> currentCanteens = existing.getCanteens() != null ? new ArrayList<>(existing.getCanteens())
                    : new ArrayList<>();

            // 获取新的食堂列表
            List<Canteen> newCanteens = canteenIds.isEmpty() ? new ArrayList<>()
                    : canteenRepository.findAllById(canteenIds);

            // 检查食堂是否存在
            if (!canteenIds.isEmpty() && newCanteens.size() != canteenIds.size()) {
                List<String> existingIds = newCanteens.stream().map(Canteen::getCanteenId).collect(Collectors.toList());
                String missingIds = canteenIds.stream()
                        .filter(id -> !existingIds.contains(id))
                        .collect(Collectors.joining(","));
                throw new IllegalArgumentException("食堂ID不存在：" + missingIds);
            }

            // 从原食堂中移除关系
            for (Canteen canteen : currentCanteens) {
                if (canteen.getTicketTypes() != null) {
                    canteen.getTicketTypes().remove(existing);
                    canteenRepository.save(canteen);
                }
            }

            // 向新食堂添加关系
            for (Canteen canteen : newCanteens) {
                if (canteen.getTicketTypes() == null) {
                    canteen.setTicketTypes(new ArrayList<>());
                }
                if (!canteen.getTicketTypes().contains(existing)) {
                    canteen.getTicketTypes().add(existing);
                }
                canteenRepository.save(canteen);
            }

            // 更新餐票类型中的食堂引用
            existing.setCanteens(newCanteens);
        }

        return ticketTypeRepository.save(existing);
    }

    // 分页获取餐票类型
    public Page<TicketTypeEntity> getTicketTypesByPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return ticketTypeRepository.findAll(pageable);
    }

    // 切换餐票类型状态
    public void toggleTicketTypeStatus(String typeId, Integer status) {
        TicketTypeEntity ticketType = ticketTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("餐票类型ID不存在"));

        // 验证状态值只能是0或1
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("无效的状态值");
        }

        ticketType.setStatus(status);
        ticketTypeRepository.save(ticketType);
    }
}