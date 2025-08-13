package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Dto.DraftOrderDTO;

import com.Digital.meal.tickets.demo.Dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 草稿订单服务类
 * 负责管理订单缓存功能
 */
@Service
public class DraftOrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderService orderService;

    // 缓存键前缀
    private static final String DRAFT_KEY_PREFIX = "draft_order:";
    private static final String USER_DRAFTS_KEY_PREFIX = "user_drafts:";
    
    // 默认缓存过期时间（24小时）
    private static final long DEFAULT_EXPIRE_HOURS = 24;

    /**
     * 创建草稿订单
     */
    public DraftOrderDTO createDraftOrder(String userId, String agenciesId, HttpServletRequest request) {
        DraftOrderDTO draft = new DraftOrderDTO(userId, agenciesId);
        draft.setCreateIP(getClientIP(request));
        
        // 保存到Redis
        saveDraftToCache(draft);
        
        // 添加到用户草稿列表
        addToUserDraftsList(userId, draft.getDraftId());
        
        return draft;
    }

    /**
     * 保存草稿订单到缓存
     */
    public DraftOrderDTO saveDraftOrder(DraftOrderDTO draft) {
        if (draft == null || draft.getDraftId() == null) {
            throw new IllegalArgumentException("草稿订单或草稿ID不能为空");
        }
        
        draft.updateTimestamp();
        saveDraftToCache(draft);
        
        return draft;
    }







    /**
     * 获取草稿订单
     */
    public DraftOrderDTO getDraftOrder(String draftId) {
        String key = DRAFT_KEY_PREFIX + draftId;
        Object obj = redisTemplate.opsForValue().get(key);
        
        if (obj instanceof DraftOrderDTO) {
            return (DraftOrderDTO) obj;
        }
        return null;
    }

    /**
     * 获取用户的所有草稿订单
     */
    public List<DraftOrderDTO> getUserDraftOrders(String userId) {
        String userDraftsKey = USER_DRAFTS_KEY_PREFIX + userId;
        Set<Object> draftIds = redisTemplate.opsForSet().members(userDraftsKey);
        
        if (draftIds == null || draftIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DraftOrderDTO> drafts = new ArrayList<>();
        for (Object draftIdObj : draftIds) {
            String draftId = draftIdObj.toString();
            DraftOrderDTO draft = getDraftOrder(draftId);
            if (draft != null) {
                drafts.add(draft);
            } else {
                // 清理无效的草稿ID
                redisTemplate.opsForSet().remove(userDraftsKey, draftId);
            }
        }
        
        // 按更新时间倒序排列
        drafts.sort((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()));
        
        return drafts;
    }

    /**
     * 获取用户当前未完成的订单（最新的草稿）
     */
    public DraftOrderDTO getCurrentUserDraft(String userId) {
        List<DraftOrderDTO> drafts = getUserDraftOrders(userId);

        // 过滤出未提交的草稿（状态不是2）
        List<DraftOrderDTO> pendingDrafts = drafts.stream()
            .filter(draft -> draft.getStatus() != 2) // 状态2表示已提交
            .toList();

        // 返回最新的未完成草稿
        return pendingDrafts.isEmpty() ? null : pendingDrafts.get(0);
    }

    /**
     * 删除草稿订单
     */
    public boolean deleteDraftOrder(String draftId) {
        DraftOrderDTO draft = getDraftOrder(draftId);
        if (draft == null) {
            return false;
        }
        
        // 从Redis中删除草稿
        String key = DRAFT_KEY_PREFIX + draftId;
        redisTemplate.delete(key);
        
        // 从用户草稿列表中移除
        String userDraftsKey = USER_DRAFTS_KEY_PREFIX + draft.getUserId();
        redisTemplate.opsForSet().remove(userDraftsKey, draftId);
        
        return true;
    }

    /**
     * 提交草稿订单（转换为正式订单）
     */
    public OrderDTO submitDraftOrder(String draftId, HttpServletRequest request) {
        DraftOrderDTO draft = getDraftOrder(draftId);
        if (draft == null) {
            throw new IllegalArgumentException("草稿订单不存在: " + draftId);
        }
        
        if (!draft.isValid()) {
            throw new IllegalArgumentException("草稿订单数据无效，无法提交");
        }
        
        // 转换为OrderService需要的格式
        List<OrderService.CreateOrderItemRequest> orderItems = draft.getOrderItems().stream()
                .map(item -> {
                    OrderService.CreateOrderItemRequest req = new OrderService.CreateOrderItemRequest();
                    req.setTypeId(item.getTypeId());
                    req.setAmount(item.getAmount());
                    return req;
                })
                .collect(Collectors.toList());
        
        try {
            // 创建正式订单
            OrderDTO order = orderService.createOrderWithItems(orderItems, request);
            
            // 标记草稿为已提交
            draft.setStatus(2);
            draft.updateTimestamp();
            saveDraftToCache(draft);
            
            // 可选：删除已提交的草稿（或保留一段时间作为历史记录）
            // deleteDraftOrder(draftId);
            
            return order;
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
            throw new RuntimeException("提交订单失败: " + errorMessage, e);
        }
    }

    /**
     * 清理过期的草稿订单
     */
    public int cleanExpiredDrafts() {
        // 这个方法可以通过定时任务调用
        // 实现逻辑：扫描所有草稿，删除超过一定时间的草稿
        // 由于Redis会自动过期，这里主要是清理用户草稿列表中的无效引用
        
        // 简单实现：可以通过扫描用户草稿列表来清理
        return 0; // 返回清理的数量
    }

    // ========== 私有辅助方法 ==========

    /**
     * 保存草稿到缓存
     */
    private void saveDraftToCache(DraftOrderDTO draft) {
        String key = DRAFT_KEY_PREFIX + draft.getDraftId();
        redisTemplate.opsForValue().set(key, draft, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 添加到用户草稿列表
     */
    private void addToUserDraftsList(String userId, String draftId) {
        String userDraftsKey = USER_DRAFTS_KEY_PREFIX + userId;
        redisTemplate.opsForSet().add(userDraftsKey, draftId);
        // 设置用户草稿列表的过期时间（比单个草稿稍长）
        redisTemplate.expire(userDraftsKey, DEFAULT_EXPIRE_HOURS + 1, TimeUnit.HOURS);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip != null ? ip : "127.0.0.1";
    }
}
