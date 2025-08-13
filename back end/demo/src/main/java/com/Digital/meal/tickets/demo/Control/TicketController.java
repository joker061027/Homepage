package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Common.Result;
import com.Digital.meal.tickets.demo.Dto.CreateTicketRequest;
import com.Digital.meal.tickets.demo.Dto.TicketDTO;
import com.Digital.meal.tickets.demo.Entity.Ticket;
import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;
import com.Digital.meal.tickets.demo.Service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  // 标识为REST接口控制器
@RequestMapping("/tickets")  // 基础路径


public class TicketController {
    @Autowired
    private TicketService ticketService;


    
    // 查询ticket表全部信息
    @GetMapping("/selectAll")
    public ResponseEntity<List<TicketDTO>> getselectAllTickets(){
        List<TicketDTO> selectAllTickets = ticketService.getselectAllTickets();
        return new ResponseEntity<>(selectAllTickets, HttpStatus.OK);
    }

    // 根据ID查询单个餐票
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable String id) {
        TicketDTO ticketDTO = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticketDTO);
    }


    // 条件查询餐票：GET /tickets?canteenId=xxx
    @GetMapping
    public ResponseEntity<List<TicketDTO>> queryTickets(@RequestParam String canteenId) {
        List<TicketDTO> tickets = ticketService.queryTickets(canteenId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    /**
     * 创建餐票接口
     */
    @PostMapping("/create")
    public Result<List<TicketDTO>> createTickets(
            @RequestBody TicketDTO.CreateTicketRequest request,
            HttpServletRequest httpRequest) {
        try {
            List<TicketDTO> tickets = ticketService.createTickets(request, httpRequest);
            return Result.success("餐票创建成功", tickets);
        } catch (Exception e) {
            return Result.error("餐票创建失败：" + e.getMessage());
        }
    }


    /**
     * 检查餐票有效性
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> checkTicket(@RequestParam String ticketNumber) {
        TicketDTO ticket = ticketService.checkTicket(ticketNumber);

        Map<String, Object> data = new HashMap<>();
        if (ticket == null) {
            data.put("valid", false);
            data.put("message", "餐票不存在");
        } else if ("99".equals(ticket.getStaus())) { // 99对应已核销（现有状态定义）
            data.put("valid", false);
            data.put("message", "餐票已使用");
        } else if (isExpired(ticket.getExpirationTime())) { // 检查过期（适配字符串时间格式）
            data.put("valid", false);
            data.put("message", "餐票已过期");
        } else {
            data.put("valid", true);
            data.put("message", "餐票有效");
            data.put("ticket", ticket);
        }
        return Result.success("检查成功", data);
    }

    /**
     * 使用餐票（核销操作，适配现有代码规范）
     */
    @PostMapping("/use")
    public Result<Map<String, Object>> useTicket(
            @RequestParam String tickerId,
            @RequestParam String stallId,
            @RequestParam String usedId) {

        TicketDTO ticket = ticketService.useTicket(tickerId, stallId, usedId);

        Map<String, Object> data = new HashMap<>();
        if (ticket != null) {
            data.put("ticket", ticket);
            return Result.success("餐票使用成功", data);
        } else {
            return Result.error("餐票使用失败，可能已使用或过期");
        }
    }


    // 辅助方法：检查过期（适配现有字符串时间格式 yyyyMMdd 或 yyyyMMddHHmmss）
    private boolean isExpired(String expirationTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(expirationTime.length() == 8 ? "yyyyMMdd" : "yyyyMMddHHmmss");
            Date expireDate = sdf.parse(expirationTime);
            return expireDate.before(new Date());
        } catch (ParseException e) {
            return true; // 格式错误视为过期
        }
    }




    /**
     * 核销餐票：更新状态为99，记录使用信息
     * @param id 餐票ID
     * @param verifyInfo 核销信息（包含档口ID、档口人员ID、核销人ID）
     * @return 核销结果
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<Boolean> verifyTicket(
            @PathVariable String id,
            @RequestBody TicketDTO.VerifyTicket verifyInfo) {
        boolean result = ticketService.verifyTicket(id, verifyInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 批量核销餐票
     * @param batchVerify 批量核销参数
     * @return 包含核销成功数量的响应
     */
    @PostMapping("/batch-verify")
    public ResponseEntity<Result<Integer>> batchVerifyTickets(@RequestBody TicketDTO.BatchVerify batchVerify) {
        int successCount = ticketService.batchVerifyTickets(batchVerify);
        if (successCount > 0) {
            return ResponseEntity.ok(Result.success("批量核销成功", successCount));
        } else {
            return ResponseEntity.ok(Result.error("未核销任何餐票（可能已核销或ID不存在）"));
        }
    }


    // 获取餐票类型列表：GET /ticket-types
    @GetMapping("/ticket-types")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypes() {
        List<TicketTypeEntity> ticketTypes = ticketService.getTicketTypes();
        return new ResponseEntity<>(ticketTypes, HttpStatus.OK);
    }

    /**
     * 查询已核销的餐票
     * @return 包含餐票列表和数量的响应
     */
    @GetMapping("/verified")
    public ResponseEntity<Map<String, Object>> getVerifiedTickets() {
        Map<String, Object> result = ticketService.getVerifiedTickets();
        return ResponseEntity.ok(result);
    }

    /**
     * 查询未核销的餐票
     * @return 包含餐票列表和数量的响应
     */
    @GetMapping("/unverified")
    public ResponseEntity<Map<String, Object>> getUnverifiedTickets() {
        Map<String, Object> result = ticketService.getUnverifiedTickets();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count-verified-status")
    public Result<List<Map<String, Object>>> getVerifiedAndUnverifiedCounts() {
        try {
            List<Map<String, Object>> result = ticketService.getVerifiedAndUnverifiedCounts();
            return Result.success("统计成功", result);
        } catch (Exception e) {
            return Result.error("统计失败：" + e.getMessage());
        }
    }

    // 按代理商汇总未核销餐票数量（agenciesId去重）
    @GetMapping("/unverified/sum-by-agency")
    public Result<List<Map<String, Object>>> getSumUnverifiedByAgency() {
        try {
            List<Map<String, Object>> result = ticketService.getSumUnverifiedByAgency();
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

}
