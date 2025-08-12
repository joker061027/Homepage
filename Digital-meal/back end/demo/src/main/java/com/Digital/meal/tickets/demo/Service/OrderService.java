package com.Digital.meal.tickets.demo.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Digital.meal.tickets.demo.Dto.OrderDTO;
import com.Digital.meal.tickets.demo.Dto.Orderandott;
import com.Digital.meal.tickets.demo.Dto.PageResult;
import com.Digital.meal.tickets.demo.Entity.OrderEntity;
import com.Digital.meal.tickets.demo.Repository.OrderRepository;
import com.Digital.meal.tickets.demo.Repository.OttRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OttRepository ottRepository;

    @Autowired
    private OttService ottService;

    /**
     * 获取所有订单
     */
    public List<OrderDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== 分页查询方法 ==========

    /**
     * 分页获取所有订单
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段（默认：createDatetime）
     * @param sortDir 排序方向（asc/desc，默认：desc）
     * @return 分页结果
     */
    public PageResult<OrderDTO> getOrdersWithPagination(int page, int size, String sortBy, String sortDir) {
        // 创建排序对象
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createDatetime");

        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size, sort);

        // 执行分页查询
        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);

        // 转换为DTO
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建分页结果
        return new PageResult<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isFirst(),
                orderPage.isLast()
        );
    }

    /**
     * 根据用户ID分页查询订单
     */
    public PageResult<OrderDTO> getOrdersByUserIdWithPagination(String userId, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createDatetime");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> orderPage = orderRepository.findByStartUserId(userId, pageable);

        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isFirst(),
                orderPage.isLast()
        );
    }

    /**
     * 根据状态分页查询订单
     */
    public PageResult<OrderDTO> getOrdersByStatusWithPagination(Integer status, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createDatetime");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> orderPage = orderRepository.findByStatus(status, pageable);

        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isFirst(),
                orderPage.isLast()
        );
    }

    /**
     * 根据订单ID获取订单
     */
    public Optional<OrderDTO> getOrderById(String orderId) {
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        return order.map(this::convertToDTO);
    }

    /**
     * 根据订单ID获取订单实体（用于关联查询）
     */
    public Optional<OrderEntity> getOrderEntityById(String orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * 根据用户ID获取订单列表
     */
    public List<OrderDTO> getOrdersByUserId(String userId) {
        List<OrderEntity> orders = orderRepository.findByStartUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据订单号获取订单详情（包含订单项）
     */
    public Map<String, Object> getOrderDetailsByNumber(String orderNumber) {
        // 根据订单号查找订单
        Optional<OrderEntity> orderOpt = orderRepository.findByOrderNumber(orderNumber);
        if (!orderOpt.isPresent()) {
            return null;
        }

        OrderEntity order = orderOpt.get();
        OrderDTO orderDTO = convertToDTO(order);

        // 获取订单项详情
        List<Object[]> orderItemsData = ottRepository.findOrderItemsWithTypeInfo(order.getOrderId());

        List<Map<String, Object>> orderItems = new ArrayList<>();
        int totalAmount = 0;
        double totalValue = 0.0;

        for (Object[] row : orderItemsData) {
            Map<String, Object> item = new HashMap<>();
            item.put("typeId", row[1]); // TypeID
            item.put("typeName", row[2]); // TypeName
            item.put("amount", row[3]); // OredrAmount
            item.put("status", row[4]); // Status
            item.put("unitValue", row[8]); // Value from tickettype

            int amount = (Integer) row[3];
            double unitValue = row[8] != null ? ((Number) row[8]).doubleValue() : 0.0;
            double itemTotal = amount * unitValue;

            item.put("totalValue", itemTotal);

            totalAmount += amount;
            totalValue += itemTotal;

            orderItems.add(item);
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("order", orderDTO);
        result.put("orderItems", orderItems);
        result.put("summary", Map.of(
            "totalTickets", totalAmount,
            "totalValue", totalValue,
            "itemCount", orderItems.size()
        ));

        return result;
    }

    /**
     * 根据机构ID获取订单列表
     */
    public List<OrderDTO> getOrdersByAgenciesId(String agenciesId) {
        List<OrderEntity> orders = orderRepository.findByAgenciesId(agenciesId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态获取订单列表
     */
    public List<OrderDTO> getOrdersByStatus(Integer status) {
        List<OrderEntity> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建新订单 - 用户申请版本
     */
    public OrderDTO createOrder(CreateOrderRequest request, HttpServletRequest httpRequest) {
        OrderEntity order = new OrderEntity();

        // 系统自动生成的字段
        order.setOrderId(generateOrderId());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(0); // 默认待审核状态
        order.setCreateDatetime(getCurrentDateTime());
        // 注意：根据数据库结构，设置CreateIP字段
        order.setCreateIP("127.0.0.1"); // 默认IP地址，实际应用中应该获取真实IP
        order.setSuggestion(""); // 审核建议初始为空，等待管理员填写

        // 从登录用户获取的字段
        String currentUserId = getCurrentUserId();
        String currentAgenciesId = getUserAgenciesId(currentUserId);

        order.setUserId(currentUserId);
        order.setAgenciesId(currentAgenciesId);
        order.setFkCreator(currentAgenciesId); // 使用FK_Creator字段
        // order.setCreator(currentAgenciesId); // 使用Creator字段 - 暂时注释

        // 用户输入的字段
        order.setExpirationTime(request.getExpirationTime());

        OrderEntity savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    /**
     * 创建订单（包含订单项） - 推荐使用此方法
     */
    @Transactional
    public OrderDTO createOrderWithItems(List<CreateOrderItemRequest> orderItems, HttpServletRequest httpRequest) {
        // 验证订单项
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }

        // 创建订单
        OrderEntity order = new OrderEntity();

        // 系统自动生成的字段
        order.setOrderId(generateOrderId());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(0); // 默认待审核状态
        order.setCreateDatetime(getCurrentDateTime());
        order.setCreateIP(getClientIP(httpRequest)); // 获取真实IP
        order.setSuggestion(""); // 审核建议初始为空，等待管理员填写

        // 从登录用户获取的字段
        String currentUserId = getCurrentUserId();
        String currentAgenciesId = getUserAgenciesId(currentUserId);

        order.setUserId(currentUserId);
        order.setAgenciesId(currentAgenciesId);
        order.setFkCreator(currentAgenciesId);

        // 设置必填字段
        order.setPassUserId("USER001"); // 临时设置审批人，实际应根据业务逻辑确定
        order.setPassTime(""); // 审批时间初始为空，审批时再设置

        // 自动设置过期时间（默认7天后过期）
        order.setExpirationTime(getDefaultExpirationTime());

        // 保存订单
        OrderEntity savedOrder = orderRepository.save(order);

        // 创建订单项
        for (CreateOrderItemRequest item : orderItems) {
            if (item.getTypeId() != null && item.getAmount() != null && item.getAmount() > 0) {
                ottService.createOrderItem(savedOrder.getOrderId(), item.getTypeId(), item.getAmount());
            }
        }

        return convertToDTO(savedOrder);
    }

    /**
     * 简化版创建订单 - 只需要餐票类型和数量（保持向后兼容）
     * @deprecated 推荐使用 createOrderWithItems 方法
     */
    @Deprecated
    public OrderDTO createSimpleOrder(List<CreateOrderItemRequest> orderItems, HttpServletRequest httpRequest) {
        return createOrderWithItems(orderItems, httpRequest);
    }

    /**
     * 管理员审核订单 - 填写审核建议
     */
    public OrderDTO reviewOrder(String orderId, ReviewOrderRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 只有管理员可以修改审核状态和建议
        order.setStatus(request.getStatus()); // 1:通过, -1:拒绝
        order.setSuggestion(request.getSuggestion()); // 审核建议
        
        OrderEntity savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    /**
     * 更新订单
     */
    public Optional<OrderDTO> updateOrder(String orderId, OrderDTO orderDTO) {
        Optional<OrderEntity> existingOrder = orderRepository.findById(orderId);
        
        if (existingOrder.isPresent()) {
            OrderEntity order = existingOrder.get();
            
            if (orderDTO.getAgenciesId() != null) order.setAgenciesId(orderDTO.getAgenciesId());
            if (orderDTO.getUserId() != null) order.setUserId(orderDTO.getUserId());
            if (orderDTO.getStatus() != null) order.setStatus(orderDTO.getStatus());
            if (orderDTO.getSuggestion() != null) order.setSuggestion(orderDTO.getSuggestion());
            if (orderDTO.getExpirationTime() != null) order.setExpirationTime(orderDTO.getExpirationTime());
            
            OrderEntity savedOrder = orderRepository.save(order);
            return Optional.of(convertToDTO(savedOrder));
        }
        
        return Optional.empty();
    }

    /**
     * 删除订单
     */
    public boolean deleteOrder(String orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }

    /**
     * 获取订单统计信息
     */
    public Map<String, Object> getOrderStatistics(String orderId) {
        List<com.Digital.meal.tickets.demo.Entity.Ott> orderItems = ottRepository.findByOrderId(orderId);

        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("orderId", orderId);
        statistics.put("totalItems", orderItems.size());
        statistics.put("totalQuantity", orderItems.stream().mapToInt(ott -> ott.getOrderAmount()).sum());

        return statistics;
    }

    /**
     * 转换实体为DTO
     */
    private OrderDTO convertToDTO(OrderEntity order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setAgenciesId(order.getAgenciesId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setCreator(order.getFkCreator()); // 从FK_Creator字段获取，因为Creator字段被注释了
        dto.setCreateDatetime(order.getCreateDatetime());
        dto.setCreateIP(order.getCreateIP()); // 从CreateIP字段获取
        dto.setSuggestion(order.getSuggestion()); // 从Suggestion字段获取
        dto.setExpirationTime(order.getExpirationTime());
        return dto;
    }

    /**
     * 转换实体为DTO（包含关联信息）
     */
    @Transactional(readOnly = true)
    public OrderDTO convertToDTOWithDetails(OrderEntity order) {
        OrderDTO dto = convertToDTO(order);

        // 添加关联信息
        if (order.getAgencies() != null) {
            dto.setAgenciesName(order.getAgencies().getAgenciesName());
        }

        if (order.getStartUser() != null) {
            dto.setStartUserName(order.getStartUser().getFullName());
        }

        if (order.getPassUser() != null) {
            dto.setPassUserName(order.getPassUser().getFullName());
        }

        // 添加订单项数量
        if (order.getOrderItems() != null) {
            dto.setOrderItemsCount(order.getOrderItems().size());
        }

        // 添加餐票数量
        if (order.getTickets() != null) {
            dto.setTicketsCount(order.getTickets().size());
        }

        return dto;
    }

    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        // 简单的ID生成逻辑，实际项目中可能需要更复杂的逻辑
        long count = orderRepository.count();
        return String.format("O%03d", count + 1);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        // 使用UUID生成唯一订单号，取前7位确保总长度不超过10位
        // 格式：ORD + UUID前7位
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "ORD" + uuid.substring(0, 7);
    }

    /**
     * 获取当前日期时间
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 获取默认过期时间（7天后）
     */
    private String getDefaultExpirationTime() {
        return LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 获取当前用户ID - 临时实现，实际项目中应从安全上下文获取
     */
    private String getCurrentUserId() {
        // TODO: 从Spring Security或JWT token中获取当前用户ID
        return "USER001"; // 临时硬编码，使用已知存在的用户ID
    }

    /**
     * 根据用户ID获取机构ID - 临时实现，实际项目中应查询用户表
     */
    private String getUserAgenciesId(String userId) {
        // TODO: 从用户表中查询用户所属机构ID
        // 当前userId参数未使用，因为是临时硬编码实现
        // 在实际实现中，应该根据userId查询数据库获取对应的机构ID
        return "DEPT001"; // 临时硬编码，使用已知存在的部门ID
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
    
    //首页餐票订单 - 无分页
    public List<Orderandott> getOrderandott(){
        try {
            List<Object[]> results = orderRepository.findOrderSummaries();
            List<Orderandott> orderList = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                Orderandott dto = convertToOrderandott(results.get(i));
                dto.setSequence(i + 1); // 设置序号
                orderList.add(dto);
            }

            return orderList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // 数值类型转换
    private Integer convertToInteger(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof java.math.BigDecimal) return ((java.math.BigDecimal) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 将查询结果转换为Orderandott DTO
    private Orderandott convertToOrderandott(Object[] row) {
        Orderandott dto = new Orderandott();
        dto.setOrderNumber((String) row[0]);
        dto.setCreateDatetime((String) row[1]);
        dto.setTotalQuantity(convertToInteger(row[2]));
        dto.setUsedQuantity(convertToInteger(row[3]));
        dto.setStatus((Integer) row[4]);
        return dto;
    }

    /**
     * 首页餐票订单 - 分页版本（按createDatetime降序排序）
     * @param page 页码（从0开始）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（忽略，固定按createDatetime降序）
     * @param sortDir 排序方向（忽略，固定降序）
     * @return 分页结果
     */
    public PageResult<Orderandott> getOrderandottWithPagination(int page, int size, String sortBy, String sortDir) {
        try {
            // 先获取所有数据并转换为DTO
            List<Object[]> allResults = orderRepository.findOrderSummaries();
            List<Orderandott> allOrderList = new ArrayList<>();

            // 转换为DTO（暂不设置序号）
            for (Object[] result : allResults) {
                Orderandott dto = convertToOrderandott(result);
                allOrderList.add(dto);
            }

            // 按createDatetime降序排序
            allOrderList.sort((o1, o2) -> {
                String date1 = o1.getCreateDatetime();
                String date2 = o2.getCreateDatetime();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1); // 降序排序
            });

            // 排序后设置序号
            for (int i = 0; i < allOrderList.size(); i++) {
                allOrderList.get(i).setSequence(i + 1);
            }

            // 计算分页参数
            int totalElements = allOrderList.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalElements);

            // 获取当前页数据
            List<Orderandott> pageData = new ArrayList<>();
            if (startIndex < totalElements) {
                pageData = allOrderList.subList(startIndex, endIndex);
            }

            // 构建分页结果
            return new PageResult<>(
                    pageData,
                    page,
                    size,
                    (long) totalElements,
                    totalPages,
                    page == 0,
                    page >= totalPages - 1
            );
        } catch (Exception e) {
            return new PageResult<>(new ArrayList<>(), page, size, 0L, 0, true, true);
        }
    }




    // 内部类定义
    public static class CreateOrderRequest {
        private String expirationTime;
        private List<CreateOrderItemRequest> orderItems;

        public String getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(String expirationTime) {
            this.expirationTime = expirationTime;
        }

        public List<CreateOrderItemRequest> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<CreateOrderItemRequest> orderItems) {
            this.orderItems = orderItems;
        }
    }

    public static class CreateOrderItemRequest {
        private String typeId;
        private Integer amount;

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    public static class ReviewOrderRequest {
        private Integer status; // 1:通过, -1:拒绝
        private String suggestion; // 审核建议

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
}

