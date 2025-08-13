package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Dto.OrderDTO;
import com.Digital.meal.tickets.demo.Dto.PageResult;
import com.Digital.meal.tickets.demo.Dto.DraftOrderDTO;
import com.Digital.meal.tickets.demo.Entity.OrderEntity;

import com.Digital.meal.tickets.demo.Service.OrderService;
import com.Digital.meal.tickets.demo.Service.DraftOrderService;
import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Digital.meal.tickets.demo.Common.Result;
import com.Digital.meal.tickets.demo.Dto.DraftOrderDTO;
import com.Digital.meal.tickets.demo.Dto.OrderDTO;
import com.Digital.meal.tickets.demo.Dto.OrderDetailDTO;
import com.Digital.meal.tickets.demo.Dto.Orderandott;
import com.Digital.meal.tickets.demo.Dto.PageResult;
import com.Digital.meal.tickets.demo.Entity.OrderEntity;
import com.Digital.meal.tickets.demo.Repository.OrderRepository;
import com.Digital.meal.tickets.demo.Service.DraftOrderService;
import com.Digital.meal.tickets.demo.Service.OrderService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单控制器 - 提供订单相关的REST API接口
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final String DEFAULT_USER_ID = "U002";
    private static final String DEFAULT_AGENCY_ID = "A001";

    @Autowired
    private OrderService orderService;

    @Autowired
    private DraftOrderService draftOrderService;

    @Autowired
    private OrderRepository orderRepository;

    // ========== 基础API端点 ==========



    /**
     * 获取所有订单的方法
     * 访问路径：GET /orders
     * 返回系统中的所有订单列表
     */
    // @GetMapping注解处理GET请求，无额外路径（使用控制器基础路径）
    @GetMapping
    public Result<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return Result.success("获取订单列表成功", orders);
    }

    /**
     * 根据订单ID获取单个订单的方法
     * 访问路径：GET /orders/{orderId}
     * @param orderId 订单ID，从URL路径中获取
     * @return 包含订单信息的结果对象
     */
    // @GetMapping注解处理GET请求，{orderId}是路径变量
    @GetMapping("/{orderId}")
    public Result<OrderDTO> getOrderById(@PathVariable String orderId) {
        Optional<OrderDTO> order = orderService.getOrderById(orderId);
        if (order.isPresent()) {
            return Result.success("获取订单成功", order.get());
        } else {
            throw new RuntimeException("订单不存在");
        }
    }




    @GetMapping("/{orderId}/details")
    public Result<OrderDTO> getOrderByIdWithDetails(@PathVariable String orderId) {
        try {
            Optional<OrderEntity> orderOpt = orderService.getOrderEntityById(orderId);
            if (orderOpt.isPresent()) {
                OrderDTO orderDTO = orderService.convertToDTOWithDetails(orderOpt.get());
                return Result.success("获取订单详细信息成功", orderDTO);
            } else {
                return Result.error("订单不存在");
            }
        } catch (Exception e) {
            return Result.error("获取订单详细信息失败: " + e.getMessage());
        }
    }



    /**
     * 根据用户ID获取该用户的所有订单列表
     * 访问路径：GET /orders/user/{userId}
     * @param userId 用户ID，从URL路径中获取
     * @return 包含用户订单列表的响应实体
     */
    // @GetMapping注解处理GET请求，路径包含用户ID参数
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable String userId) {
        // 调用服务层方法根据用户ID查询订单
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        // 使用ResponseEntity.ok()返回200状态码和订单列表
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据机构ID获取该机构的所有订单列表
     * 访问路径：GET /orders/agency/{agenciesId}
     * @param agenciesId 机构ID，从URL路径中获取
     * @return 包含机构订单列表的响应实体
     */
    // @GetMapping注解处理GET请求，路径包含机构ID参数
    @GetMapping("/agency/{agenciesId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByAgenciesId(@PathVariable String agenciesId) {
        // 调用服务层方法根据机构ID查询订单
        List<OrderDTO> orders = orderService.getOrdersByAgenciesId(agenciesId);
        // 返回200状态码和订单列表
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据订单状态获取订单列表
     * 访问路径：GET /orders/status/{status}
     * @param status 订单状态（0=待审核，1=已通过，-1=已拒绝）
     * @return 包含指定状态订单列表的响应实体
     */
    // @GetMapping注解处理GET请求，路径包含状态参数
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable Integer status) {
        // 调用服务层方法根据状态查询订单
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        // 返回200状态码和订单列表
        return ResponseEntity.ok(orders);
    }

    // ========== 智能订单创建API ==========

    /**
     * 智能订单创建方法 - 支持自动草稿保存和实时编辑
     * 这是推荐的订单创建方式，具有以下特性：
     * 1. 支持从现有草稿继续创建
     * 2. 自动创建草稿保存用户输入
     * 3. 支持自动提交或手动提交
     * 访问路径：POST /orders
     * @param request 智能创建订单请求对象，包含用户ID、机构ID、订单项等信息
     * @param httpRequest HTTP请求对象，用于获取客户端IP等信息
     * @return 智能订单响应，包含订单或草稿信息
     */
    // @PostMapping注解处理POST请求，用于创建资源
    @PostMapping
    public Result<SmartOrderResponse> createOrder(
        // @RequestBody注解将HTTP请求体中的JSON转换为Java对象
        @RequestBody SmartCreateOrderRequest request,
        // HttpServletRequest用于获取HTTP请求的详细信息
        HttpServletRequest httpRequest) {

        try {
            // 检查是否提供了草稿ID，如果有则从草稿继续创建
            if (request.getDraftId() != null && !request.getDraftId().trim().isEmpty()) {
                // 调用草稿服务从现有草稿提交订单
                OrderDTO order = draftOrderService.submitDraftOrder(request.getDraftId(), httpRequest);
                // 返回成功结果，包含已提交的订单信息
                return Result.success("订单创建成功", new SmartOrderResponse(order, null, "submitted"));
            }

            // 如果没有草稿ID，创建新的订单流程
            String userId = request.getUserId() != null ? request.getUserId() : DEFAULT_USER_ID;
            String agenciesId = request.getAgenciesId() != null ? request.getAgenciesId() : DEFAULT_AGENCY_ID;

            // 调用草稿服务创建新的草稿订单
            DraftOrderDTO draft = draftOrderService.createDraftOrder(userId, agenciesId, httpRequest);

            // 检查是否提供了订单项数据
            if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
                // 将订单项设置到草稿中
                draft.setOrderItems(request.getOrderItems());
                // 保存草稿到Redis缓存
                draft = draftOrderService.saveDraftOrder(draft);

                // 检查是否设置了自动提交标志
                if (request.isAutoSubmit()) {
                    // 自动提交：将草稿转换为正式订单并保存到数据库
                    OrderDTO order = draftOrderService.submitDraftOrder(draft.getDraftId(), httpRequest);
                    // 返回成功结果，包含已提交的订单信息
                    return Result.success("订单创建成功", new SmartOrderResponse(order, null, "submitted"));
                }
            }

            // 如果不自动提交，返回草稿信息供用户继续编辑
            return Result.success("订单草稿创建成功，可以继续编辑或提交",
                                new SmartOrderResponse(null, draft, "draft"));

        } catch (Exception e) {
            throw e; // 让全局异常处理器处理
        }
    }





    // ========== 草稿订单管理流程 ==========

    /**
     * 开始创建订单方法 - 自动创建草稿
     * 这个方法用于初始化订单创建流程，特点：
     * 1. 立即创建一个空的草稿订单
     * 2. 保存到Redis缓存中
     * 3. 返回草稿ID供后续操作使用
     * 访问路径：POST /orders/start
     * @param request 开始创建订单的请求，包含用户ID和机构ID
     * @param httpRequest HTTP请求对象，用于获取客户端信息
     * @return 包含新创建草稿信息的结果
     */
    // @PostMapping注解处理POST请求，路径为"/start"
    @PostMapping("/start")
    public Result<DraftOrderDTO> startCreateOrder(
        // @RequestBody将JSON转换为开始订单请求对象
        @RequestBody StartOrderRequest request,
        HttpServletRequest httpRequest) {

        DraftOrderDTO draft = draftOrderService.createDraftOrder(
            request.getUserId() != null ? request.getUserId() : DEFAULT_USER_ID,
            request.getAgenciesId() != null ? request.getAgenciesId() : DEFAULT_AGENCY_ID,
            httpRequest
        );

        return Result.success("订单草稿创建成功，可以开始填写订单信息", draft);
    }

    /**
     * 保存订单草稿方法（实时保存）
     * 这个方法用于实时保存用户的订单编辑内容，特点：
     * 1. 支持增量更新草稿内容
     * 2. 自动保存到Redis缓存
     * 3. 支持断点续传功能
     * 访问路径：PUT /orders/draft/{draftId}/save
     * @param draftId 草稿ID，从URL路径中获取
     * @param request 保存草稿的请求，包含要更新的订单项和过期时间
     * @param httpRequest HTTP请求对象
     * @return 包含更新后草稿信息的结果
     */
    // @PutMapping注解处理PUT请求，用于更新资源
    @PutMapping("/draft/{draftId}/save")
    public Result<DraftOrderDTO> saveOrderDraft(
        // @PathVariable从URL路径中提取草稿ID参数
        @PathVariable String draftId,
        // @RequestBody将JSON转换为保存草稿请求对象
        @RequestBody SaveDraftRequest request,
        HttpServletRequest httpRequest) {

        DraftOrderDTO draft = draftOrderService.getDraftOrder(draftId);
        if (draft == null) {
            throw new RuntimeException("订单草稿不存在，请重新开始创建订单");
        }

        if (request.getOrderItems() != null) {
            draft.setOrderItems(request.getOrderItems());
        }
        if (request.getExpirationTime() != null) {
            draft.setExpirationTime(request.getExpirationTime());
        }

        draft = draftOrderService.saveDraftOrder(draft);
        return Result.success("订单草稿保存成功", draft);
    }

    /**
     * 获取订单草稿方法
     * 根据草稿ID从Redis缓存中获取草稿详情
     * 访问路径：GET /orders/draft/{draftId}
     * @param draftId 草稿ID，从URL路径中获取
     * @return 包含草稿详情的结果对象
     */
    // @GetMapping注解处理GET请求，路径包含草稿ID参数
    @GetMapping("/draft/{draftId}")
    public Result<DraftOrderDTO> getOrderDraft(@PathVariable String draftId) {
        DraftOrderDTO draft = draftOrderService.getDraftOrder(draftId);
        if (draft == null) {
            throw new RuntimeException("订单草稿不存在或已过期");
        }
        return Result.success("获取订单草稿成功", draft);
    }

    /**
     * 提交订单方法（从草稿转为正式订单）
     * 将Redis中的草稿订单转换为MySQL中的正式订单
     * 访问路径：POST /orders/draft/{draftId}/submit
     * @param draftId 要提交的草稿ID，从URL路径中获取
     * @param httpRequest HTTP请求对象，用于获取客户端信息
     * @return 包含创建的正式订单信息的结果
     */
    // @PostMapping注解处理POST请求，用于提交草稿
    @PostMapping("/draft/{draftId}/submit")
    public Result<OrderDTO> submitOrder(
        // @PathVariable从URL路径中提取草稿ID
        @PathVariable String draftId,
        HttpServletRequest httpRequest) {

        try {
            // 调用草稿服务将草稿转换为正式订单
            // 这个过程包括：验证草稿、创建订单、创建订单项、删除草稿
            OrderDTO order = draftOrderService.submitDraftOrder(draftId, httpRequest);
            // 返回成功结果，包含新创建的订单信息
            return Result.success("订单提交成功", order);
        } catch (Exception e) {
            throw e; // 让全局异常处理器处理
        }
    }

    /**
     * 取消订单草稿方法
     * 从Redis缓存中删除指定的草稿订单
     * 访问路径：DELETE /orders/draft/{draftId}
     * @param draftId 要取消的草稿ID，从URL路径中获取
     * @return 包含删除结果的响应
     */
    // @DeleteMapping注解处理DELETE请求，用于删除资源
    @DeleteMapping("/draft/{draftId}")
    public Result<Boolean> cancelOrderDraft(@PathVariable String draftId) {
        boolean deleted = draftOrderService.deleteDraftOrder(draftId);
        if (deleted) {
            return Result.success("订单草稿已取消", true);
        } else {
            throw new RuntimeException("订单草稿不存在");
        }
    }

    /**
     * 获取用户的未完成订单草稿
     */
    @GetMapping("/drafts/user/{userId}")
    public Result<List<DraftOrderDTO>> getUserOrderDrafts(@PathVariable String userId) {
        List<DraftOrderDTO> drafts = draftOrderService.getUserDraftOrders(userId);
        List<DraftOrderDTO> pendingDrafts = drafts.stream()
            .filter(draft -> draft.getStatus() != 2) // 状态2表示已提交
            .toList();

        return Result.success("获取用户订单草稿成功", pendingDrafts);
    }

    /**
     * 获取用户当前未完成的订单（最新的草稿）
     */
    @GetMapping("/drafts/current/{userId}")
    public Result<DraftOrderDTO> getCurrentUserDraft(@PathVariable String userId) {
        DraftOrderDTO currentDraft = draftOrderService.getCurrentUserDraft(userId);
        if (currentDraft != null) {
            return Result.success("找到未完成的订单", currentDraft);
        } else {
            return Result.success("没有未完成的订单", null);
        }
    }

    /**
     * 智能开始订单创建 - 自动检测未完成订单
     * 这个API会自动检查用户是否有未完成的草稿：
     * - 如果有未完成的草稿，返回现有草稿供用户继续编辑
     * - 如果没有草稿，创建新的草稿
     * 前端调用这个API就能智能处理用户的订单创建需求
     */
    @PostMapping("/smart-start/{userId}")
    public Result<SmartStartResponse> smartStartOrder(@PathVariable String userId, HttpServletRequest httpRequest) {
        // 首先检查用户是否有未完成的草稿
        DraftOrderDTO existingDraft = draftOrderService.getCurrentUserDraft(userId);

        if (existingDraft != null) {
            // 用户有未完成的草稿，返回现有草稿信息
            SmartStartResponse response = new SmartStartResponse();
            response.setHasExistingDraft(true);
            response.setDraft(existingDraft);
            response.setMessage("您有一个未完成的订单，是否继续编辑？");
            response.setAction("CONTINUE_EXISTING");

            return Result.success("发现未完成的订单", response);
        } else {
            // 用户没有未完成的草稿，创建新的草稿
            DraftOrderDTO newDraft = draftOrderService.createDraftOrder(
                userId,
                DEFAULT_AGENCY_ID, // 可以根据用户信息获取默认机构ID
                httpRequest
            );

            SmartStartResponse response = new SmartStartResponse();
            response.setHasExistingDraft(false);
            response.setDraft(newDraft);
            response.setMessage("已为您创建新的订单草稿，请开始填写订单信息");
            response.setAction("START_NEW");

            return Result.success("创建新订单草稿成功", response);
        }
    }

    /**
     * 处理用户对现有草稿的选择
     * 当用户有未完成的草稿时，可以选择：
     * - continue: 继续编辑现有草稿
     * - restart: 删除现有草稿，重新开始
     */
    @PostMapping("/handle-existing-draft/{userId}")
    public Result<DraftOrderDTO> handleExistingDraft(
            @PathVariable String userId,
            @RequestParam String action, // "continue" 或 "restart"
            HttpServletRequest httpRequest) {

        if ("continue".equals(action)) {
            // 用户选择继续现有草稿
            DraftOrderDTO existingDraft = draftOrderService.getCurrentUserDraft(userId);
            if (existingDraft != null) {
                return Result.success("继续编辑现有订单", existingDraft);
            } else {
                throw new RuntimeException("未找到现有草稿");
            }
        } else if ("restart".equals(action)) {
            // 用户选择重新开始，先删除现有草稿
            DraftOrderDTO existingDraft = draftOrderService.getCurrentUserDraft(userId);
            if (existingDraft != null) {
                draftOrderService.deleteDraftOrder(existingDraft.getDraftId());
            }

            // 创建新的草稿
            DraftOrderDTO newDraft = draftOrderService.createDraftOrder(
                userId,
                DEFAULT_AGENCY_ID,
                httpRequest
            );

            return Result.success("已重新开始创建订单", newDraft);
        } else {
            throw new IllegalArgumentException("无效的操作类型，只支持 'continue' 或 'restart'");
        }
    }

    /**
     * 检查用户是否有未完成的订单
     */
    @GetMapping("/drafts/check/{userId}")
    public Result<UserDraftStatus> checkUserDraftStatus(@PathVariable String userId) {
        DraftOrderDTO currentDraft = draftOrderService.getCurrentUserDraft(userId);
        UserDraftStatus status = new UserDraftStatus();
        status.setUserId(userId);
        status.setHasUnfinishedOrder(currentDraft != null);
        if (currentDraft != null) {
            status.setDraftId(currentDraft.getDraftId());
            status.setCreateTime(currentDraft.getCreateTime());
            status.setUpdateTime(currentDraft.getUpdateTime());
            status.setItemCount(calculateTotalItemTypes(currentDraft.getOrderItems()));
            status.setTotalQuantity(calculateTotalQuantity(currentDraft.getOrderItems()));
        }
        return Result.success("检查完成", status);
    }

    /**
     * 管理员审核订单
     */
    @PutMapping("/{orderId}/review")
    public ResponseEntity<OrderDTO> reviewOrder(
        @PathVariable String orderId,
        @RequestBody OrderService.ReviewOrderRequest request) {

        // 这里应该验证当前用户是否为管理员
        OrderDTO reviewedOrder = orderService.reviewOrder(orderId, request);
        return ResponseEntity.ok(reviewedOrder);
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable String orderId, @RequestBody OrderDTO orderDTO) {
        Optional<OrderDTO> updatedOrder = orderService.updateOrder(orderId, orderDTO);
        return updatedOrder.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        boolean deleted = orderService.deleteOrder(orderId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 获取订单统计信息
     */
    @GetMapping("/{orderId}/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics(@PathVariable String orderId) {
        Map<String, Object> statistics = orderService.getOrderStatistics(orderId);
        return ResponseEntity.ok(statistics);
    }



    // ========== 分页查询API ==========

    /**
     * 分页获取订单列表方法
     * 这是系统的核心查询接口，支持灵活的分页、排序功能
     * 访问路径：GET /orders/page?page=0&size=10&sortBy=orderId&sortDir=desc
     * @param page 页码（从0开始计数，默认0表示第一页）
     * @param size 每页显示的记录数量（默认10条）
     * @param sortBy 排序字段名（默认按createDatetime排序）
     * @param sortDir 排序方向（asc=升序，desc=降序，默认desc）
     * @return 包含分页数据和分页信息的结果对象
     */
    // @GetMapping注解处理GET请求，路径为"/page"
    @GetMapping("/page")
    public Result<PageResult<OrderDTO>> getOrdersWithPagination(
            // @RequestParam注解从URL查询参数中获取分页参数
            // defaultValue设置默认值，当参数未提供时使用
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDatetime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResult<OrderDTO> result = orderService.getOrdersWithPagination(page, size, sortBy, sortDir);
        return Result.success("分页查询订单成功", result);
    }

    /**
     * 根据用户ID分页查询订单方法
     * 查询指定用户的所有订单，支持分页和排序
     * 访问路径：GET /orders/page/user/{userId}?page=0&size=10
     * @param userId 用户ID，从URL路径中获取
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（默认createDatetime）
     * @param sortDir 排序方向（默认desc）
     * @return 包含该用户订单的分页结果
     */
    // @GetMapping注解处理GET请求，路径包含用户ID参数
    @GetMapping("/page/user/{userId}")
    public Result<PageResult<OrderDTO>> getOrdersByUserIdWithPagination(
            // @PathVariable从URL路径中提取用户ID
            @PathVariable String userId,
            // 以下参数从查询字符串中获取，都有默认值
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDatetime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResult<OrderDTO> result = orderService.getOrdersByUserIdWithPagination(userId, page, size, sortBy, sortDir);
        return Result.success("分页查询用户订单成功", result);
    }

    /**
     * 根据订单状态分页查询订单方法
     * 查询指定状态的所有订单，常用于管理员审核界面
     * 访问路径：GET /orders/page/status/{status}?page=0&size=10
     * @param status 订单状态（0=待审核，1=已通过，-1=已拒绝）
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（默认createDatetime）
     * @param sortDir 排序方向（默认desc）
     * @return 包含指定状态订单的分页结果
     */
    // @GetMapping注解处理GET请求，路径包含状态参数
    @GetMapping("/page/status/{status}")
    public Result<PageResult<OrderDTO>> getOrdersByStatusWithPagination(
            // @PathVariable从URL路径中提取状态值
            @PathVariable Integer status,
            // 分页参数从查询字符串获取
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDatetime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResult<OrderDTO> result = orderService.getOrdersByStatusWithPagination(status, page, size, sortBy, sortDir);
        return Result.success("分页查询状态订单成功", result);
    }



    /**
     * 创建测试数据 - 仅用于开发测试
     * 访问路径：POST /orders/create-test-data
     * @param httpRequest HTTP请求对象
     * @return 创建结果
     */
    @PostMapping("/create-test-data")
    public Result<String> createTestData(HttpServletRequest httpRequest) {
        try {
            // 创建多个测试订单
            for (int i = 1; i <= 15; i++) {
                OrderService.CreateOrderItemRequest item = new OrderService.CreateOrderItemRequest();
                item.setTypeId("T001"); // 早餐券
                item.setAmount(10 + i * 5); // 不同数量

                List<OrderService.CreateOrderItemRequest> items = List.of(item);
                orderService.createOrderWithItems(items, httpRequest);
            }

            return Result.success("测试数据创建成功，已创建15个测试订单");
        } catch (Exception e) {
            return Result.error("创建测试数据失败: " + e.getMessage());
        }
    }

    /**
     * 创建简单测试数据 - 只创建订单，不创建关联数据
     * 访问路径：POST /orders/create-simple-test-data
     * @return 创建结果
     */
    @PostMapping("/create-simple-test-data")
    public Result<String> createSimpleTestData() {
        try {
            // 直接使用OrderRepository创建简单的订单数据
            for (int i = 1; i <= 10; i++) {
                OrderEntity order = new OrderEntity();
                order.setOrderId("TEST" + String.format("%03d", i));
                order.setOrderNumber("ORD" + String.format("%07d", i));
                order.setAgenciesId("A001");
                order.setStartUserId("U002");
                order.setPassUserId("U001");
                order.setStatus(i % 4 + 1); // 状态1-4循环
                order.setFkCreator("admin");
                order.setCreateDatetime("20250731");
                order.setCreateIP("127.0.0.1");
                order.setSuggestion("测试订单");
                order.setExpirationTime("20250831");
                order.setPassTime("20250731");

                orderRepository.save(order);
            }

            return Result.success("简单测试数据创建成功，已创建10个测试订单");
        } catch (Exception e) {
            return Result.error("创建简单测试数据失败: " + e.getMessage());
        }
    }

    // ========== 首页餐票订单API ==========

    /**
     * 获取首页餐票订单列表（无分页）- 保持向后兼容
     * 访问路径：GET /orders/home-tickets
     * @return 包含首页餐票订单列表的结果对象
     */
    @GetMapping("/home-tickets")
    public Result<List<Orderandott>> getHomeTicketOrders() {
        List<Orderandott> orders = orderService.getOrderandott();
        return Result.success("获取首页餐票订单成功", orders);
    }

    /**
     * 分页获取首页餐票订单列表（按DTO序号升序分页）
     * 先转换所有数据为DTO并设置序号，然后按序号升序排序进行分页
     * 访问路径：GET /orders/home-tickets/page?page=0&size=10
     * @param page 页码（从0开始计数，默认0表示第一页）
     * @param size 每页显示的记录数量（默认10条）
     * @return 包含分页数据和分页信息的结果对象，数据按DTO序号升序排列
     */
    @GetMapping("/home-tickets/page")
    public Result<PageResult<Orderandott>> getHomeTicketOrdersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResult<Orderandott> result = orderService.getOrderandottWithPagination(page, size, null, null);
        return Result.success("分页查询首页餐票订单成功", result);
    }

    /**
     * 分页获取指定用户的首页餐票订单列表（按DTO序号升序分页）
     * 访问路径：GET /orders/home-tickets/user-page?userId=xxx&page=0&size=10
     * @param userId 用户ID
     * @param page 页码（从0开始计数，默认0表示第一页）
     * @param size 每页显示的记录数量（默认10条）
     * @return 包含分页数据和分页信息的结果对象，数据按DTO序号升序排列
     */
    @GetMapping("/home-tickets/user-page")
    public Result<PageResult<Orderandott>> getUserHomeTicketOrdersWithPagination(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Orderandott> result = orderService.getOrderandottWithPaginationByUserId(userId, page, size);
        return Result.success("分页查询指定用户首页餐票订单成功", result);
    }

    // ========== TicketManagement和OrderDetailModal相关API ==========

    // 移除 TicketManagement 页面用到的包含订单项的明细接口，避免返回多余数据

    /**
     * 根据订单号获取订单详情（用于OrderDetailModal弹窗显示）
     * 访问路径：GET /orders/detail/{orderNumber}
     * @param orderNumber 订单号，从URL路径中获取
     * @return 包含订单详情的结果对象
     */
    @GetMapping("/detail/{orderNumber}")
    public Result<OrderDetailDTO> getOrderDetailByNumber(@PathVariable String orderNumber) {
        try {
            OrderDetailDTO orderDetail = orderService.getOrderDetailByNumber(orderNumber);
            if (orderDetail != null) {
                return Result.success("获取订单详情成功", orderDetail);
            } else {
                return Result.error("订单不存在");
            }
        } catch (Exception e) {
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 撤回订单 - 用于TicketManagement页面的撤回功能
     * 访问路径：PUT /orders/withdraw/{orderNumber}
     * @param orderNumber 订单号，从URL路径中获取
     * @return 撤回结果
     */
    @PutMapping("/withdraw/{orderNumber}")
    public Result<Boolean> withdrawOrder(@PathVariable String orderNumber) {
        try {
            boolean success = orderService.withdrawOrder(orderNumber);
            if (success) {
                return Result.success("订单撤回成功", true);
            } else {
                return Result.error("订单不存在或撤回失败");
            }
        } catch (Exception e) {
            return Result.error("撤回订单失败: " + e.getMessage());
        }
    }

    /**
     * 通过订单号更新订单状态为1
     * 访问路径：PUT /orders/status/{orderNumber}
     * @param orderNumber 订单号，从URL路径中获取
     * @return 更新结果
     */
    @PutMapping("/status/{orderNumber}")
    public Result<Boolean> updateOrderStatusByOrderNumber(@PathVariable String orderNumber) {
        try {
            boolean success = orderService.updateOrderStatusByOrderNumber(orderNumber);
            if (success) {
                return Result.success("订单状态更新成功", true);
            } else {
                return Result.error("订单不存在或更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }

    // ========== 请求DTO类 ==========
    // 以下是控制器内部定义的数据传输对象类，用于接收不同类型的HTTP请求

    /**
     * 开始创建订单请求类
     * 用于接收初始化订单创建流程的请求数据
     * 对应API：POST /orders/start
     */
    public static class StartOrderRequest {
        // 用户ID字段，标识是哪个用户要创建订单
        private String userId;
        // 机构ID字段，标识订单属于哪个机构
        private String agenciesId;

        // 获取用户ID的getter方法
        public String getUserId() { return userId; }
        // 设置用户ID的setter方法
        public void setUserId(String userId) { this.userId = userId; }

        // 获取机构ID的getter方法
        public String getAgenciesId() { return agenciesId; }
        // 设置机构ID的setter方法
        public void setAgenciesId(String agenciesId) { this.agenciesId = agenciesId; }
    }

    /**
     * 保存草稿请求类
     * 用于接收保存订单草稿的请求数据
     * 对应API：PUT /orders/draft/{draftId}/save
     */
    public static class SaveDraftRequest {
        // 订单项列表，包含用户选择的餐票类型和数量
        private List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems;
        // 订单过期时间，格式通常为字符串
        private String expirationTime;

        // 获取订单项列表的getter方法
        public List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> getOrderItems() {
            return orderItems;
        }
        // 设置订单项列表的setter方法
        public void setOrderItems(List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems) {
            this.orderItems = orderItems;
        }

        // 获取过期时间的getter方法
        public String getExpirationTime() { return expirationTime; }
        // 设置过期时间的setter方法
        public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime; }
    }

    /**
     * 智能创建订单请求类
     * 这是最核心的订单创建请求类，支持多种创建模式
     * 对应API：POST /orders
     */
    public static class SmartCreateOrderRequest {
        // 用户ID，标识订单创建者
        private String userId;
        // 机构ID，标识订单所属机构
        private String agenciesId;
        // 草稿ID，可选字段，用于从现有草稿继续创建订单
        private String draftId;
        // 订单项列表，包含餐票类型和数量信息
        private List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems;
        // 自动提交标志，默认false表示保存为草稿，true表示立即提交
        private boolean autoSubmit = false;
        // 订单过期时间，可选字段
        private String expirationTime;

        // 获取用户ID的getter方法
        public String getUserId() { return userId; }
        // 设置用户ID的setter方法
        public void setUserId(String userId) { this.userId = userId; }

        // 获取机构ID的getter方法
        public String getAgenciesId() { return agenciesId; }
        // 设置机构ID的setter方法
        public void setAgenciesId(String agenciesId) { this.agenciesId = agenciesId; }

        // 获取草稿ID的getter方法
        public String getDraftId() { return draftId; }
        // 设置草稿ID的setter方法，用于从现有草稿继续创建
        public void setDraftId(String draftId) { this.draftId = draftId; }

        // 获取订单项列表的getter方法
        public List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> getOrderItems() {
            return orderItems;
        }
        // 设置订单项列表的setter方法
        public void setOrderItems(List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems) {
            this.orderItems = orderItems;
        }

        // 获取自动提交标志的getter方法
        // 使用is前缀是boolean类型getter的标准命名
        public boolean isAutoSubmit() { return autoSubmit; }
        // 设置自动提交标志的setter方法
        public void setAutoSubmit(boolean autoSubmit) { this.autoSubmit = autoSubmit; }

        // 获取过期时间的getter方法
        public String getExpirationTime() { return expirationTime; }
        // 设置过期时间的setter方法
        public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime; }
    }

    /**
     * 智能订单创建响应类
     * 用于封装智能订单创建API的响应结果
     * 根据不同的创建结果返回相应的数据
     */
    public static class SmartOrderResponse {
        // 正式订单对象，当订单已提交时包含此数据
        private OrderDTO order;
        // 草稿订单对象，当订单保存为草稿时包含此数据
        private DraftOrderDTO draft;
        // 状态标识：draft表示草稿状态，submitted表示已提交状态
        private String status;
        // 提示信息，向前端用户说明操作结果
        private String message;

        // 无参构造方法，用于JSON反序列化
        public SmartOrderResponse() {}

        // 有参构造方法，用于快速创建响应对象
        // @param order 订单对象（可为null）
        // @param draft 草稿对象（可为null）
        // @param status 状态字符串
        public SmartOrderResponse(OrderDTO order, DraftOrderDTO draft, String status) {
            this.order = order;
            this.draft = draft;
            this.status = status;
            // 根据状态自动生成相应的提示信息
            this.message = generateMessage(status);
        }

        // 根据状态生成相应提示信息的私有方法
        // @param status 状态字符串
        // @return 对应的提示信息
        private String generateMessage(String status) {
            // 使用switch语句根据不同状态返回不同消息
            switch (status) {
                case "draft":
                    // 草稿状态的提示信息
                    return "订单草稿已创建，可以继续编辑或提交";
                case "submitted":
                    // 已提交状态的提示信息
                    return "订单已成功提交并写入数据库";
                default:
                    // 默认提示信息
                    return "操作完成";
            }
        }

        // 获取订单对象的getter方法
        public OrderDTO getOrder() { return order; }
        // 设置订单对象的setter方法
        public void setOrder(OrderDTO order) { this.order = order; }

        // 获取草稿对象的getter方法
        public DraftOrderDTO getDraft() { return draft; }
        // 设置草稿对象的setter方法
        public void setDraft(DraftOrderDTO draft) { this.draft = draft; }

        // 获取状态的getter方法
        public String getStatus() { return status; }
        // 设置状态的setter方法
        public void setStatus(String status) { this.status = status; }

        // 获取提示信息的getter方法
        public String getMessage() { return message; }
        // 设置提示信息的setter方法
        public void setMessage(String message) { this.message = message; }
    }



    /**
     * 用户草稿状态
     */
    public static class UserDraftStatus {
        private String userId;
        private boolean hasUnfinishedOrder;
        private String draftId;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private Integer itemCount;
        private Integer totalQuantity;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public boolean isHasUnfinishedOrder() { return hasUnfinishedOrder; }
        public void setHasUnfinishedOrder(boolean hasUnfinishedOrder) { this.hasUnfinishedOrder = hasUnfinishedOrder; }

        public String getDraftId() { return draftId; }
        public void setDraftId(String draftId) { this.draftId = draftId; }

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

        public LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

        public Integer getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
    }

    // ========== 私有计算方法 ==========

    /**
     * 计算订单项类型总数的私有方法
     * 用于统计一个订单包含多少种不同类型的餐票
     * @param orderItems 订单项列表
     * @return 订单项类型的总数量
     */
    private Integer calculateTotalItemTypes(List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems) {
        // 检查订单项列表是否为空或null
        if (orderItems == null || orderItems.isEmpty()) {
            // 如果为空，返回0
            return 0;
        }
        // 返回列表的大小，即不同餐票类型的数量
        // 例如：有早餐券和午餐券两种类型，则返回2
        return orderItems.size();
    }

    /**
     * 计算订单项总数量的私有方法
     * 用于统计一个订单包含的餐票总张数
     * @param orderItems 订单项列表
     * @return 所有订单项的数量总和
     */
    private Integer calculateTotalQuantity(List<com.Digital.meal.tickets.demo.Dto.DraftOrderItemDTO> orderItems) {
        // 检查订单项列表是否为空或null
        if (orderItems == null || orderItems.isEmpty()) {
            // 如果为空，返回0
            return 0;
        }
        // 使用Stream API计算总数量
        return orderItems.stream()
                // 将每个订单项转换为其数量值
                // 如果数量为null，则使用0作为默认值
                .mapToInt(item -> item.getAmount() != null ? item.getAmount() : 0)
                // 对所有数量求和
                .sum();
        // 例如：5张早餐券 + 3张午餐券 = 8张餐票总数
    }

    /**
     * 智能开始订单响应类
     * 用于智能开始订单API的响应，告诉前端用户的订单状态和下一步操作
     */
    public static class SmartStartResponse {
        // 是否有现有的未完成草稿
        private boolean hasExistingDraft;
        // 草稿信息（可能是现有的或新创建的）
        private DraftOrderDTO draft;
        // 给用户的提示消息
        private String message;
        // 建议的操作类型：CONTINUE_EXISTING（继续现有草稿）或 START_NEW（开始新订单）
        private String action;

        // Getters and Setters
        public boolean isHasExistingDraft() { return hasExistingDraft; }
        public void setHasExistingDraft(boolean hasExistingDraft) { this.hasExistingDraft = hasExistingDraft; }

        public DraftOrderDTO getDraft() { return draft; }
        public void setDraft(DraftOrderDTO draft) { this.draft = draft; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }

    /**
     * 简单订单创建请求类
     * 用于不依赖Redis的直接订单创建
     */
    public static class SimpleCreateOrderRequest {
        private List<SimpleOrderItem> orderItems;

        public List<SimpleOrderItem> getOrderItems() { return orderItems; }
        public void setOrderItems(List<SimpleOrderItem> orderItems) { this.orderItems = orderItems; }
    }

    /**
     * 简单订单项类
     */
    public static class SimpleOrderItem {
        private String typeId;
        private Integer amount;

        public String getTypeId() { return typeId; }
        public void setTypeId(String typeId) { this.typeId = typeId; }

        public Integer getAmount() { return amount; }
        public void setAmount(Integer amount) { this.amount = amount; }
    }
}

