package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Common.Result;
import com.Digital.meal.tickets.demo.Dto.CreateTicketRequest;
import com.Digital.meal.tickets.demo.Dto.TicketDTO;
import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;
import com.Digital.meal.tickets.demo.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  // 标识为REST接口控制器
@RequestMapping("/tickets")  // 基础路径
public class TicketController {
    @Autowired
    private TicketService ticketService;

    // 查询ticket表全部信息
    @GetMapping("selectAll")
    public ResponseEntity<List<TicketDTO>> getselectAllTickets(){
        List<TicketDTO> selectAllTickets = ticketService.getselectAllTickets();
        return new ResponseEntity<>(selectAllTickets, HttpStatus.OK);
    }

    // 根据ID查询单个餐票
    @GetMapping("/{id}")
    public Result<TicketDTO> getTicketById(@PathVariable String id) {
        try {
            TicketDTO ticket = ticketService.getTicketById(id);
            if (ticket != null) {
                return Result.success("获取餐票成功", ticket);
            } else {
                return Result.error("餐票不存在");
            }
        } catch (Exception e) {
            return Result.error("获取餐票失败: " + e.getMessage());
        }
    }

    // 创建餐票：POST /tickets
//    @PostMapping
//    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO ticketDto) {
//        TicketDTO createdTicket = ticketService.createTicket(ticketDto);
//        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
//    }

    @PostMapping("/create")
    public Result<List<TicketDTO>> createTickets(@RequestBody CreateTicketRequest request) {
        try {
            List<TicketDTO> tickets = ticketService.createTickets(request);
            return Result.success(tickets);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 条件查询餐票：GET /tickets?canteenId=xxx
    @GetMapping
    public ResponseEntity<List<TicketDTO>> queryTickets(@RequestParam String canteenId) {
        List<TicketDTO> tickets = ticketService.queryTickets(canteenId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    // 核销单张餐票：POST /tickets/{id}/cancel
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Boolean> cancelTicket(
            @PathVariable String id,
            @RequestParam String checkId) {
        boolean result = ticketService.cancelTicket(id, checkId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 批量核销餐票：POST /tickets/batch-cancel
    @PostMapping("/batch-cancel")
    public ResponseEntity<Boolean> batchCancelTickets(@RequestBody TicketDTO.BatchCancel batchCancel) {
        boolean result = ticketService.batchCancelTickets(batchCancel);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 生成打印文件：GET /tickets/{id}/print
//    @GetMapping("/{id}/print")
//    public ResponseEntity<byte[]> generatePrintFile(@PathVariable String id) {
//        byte[] fileContent = ticketService.generatePrintFile(id);
//        if (fileContent == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        // 设置响应头，指定文件下载
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=\"ticket_" + id + ".txt\"");
//        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
//    }

    // 获取餐票类型列表：GET /ticket-types
    @GetMapping("/ticket-types")
    public ResponseEntity<List<TicketTypeEntity>> getTicketTypes() {
        List<TicketTypeEntity> ticketTypes = ticketService.getTicketTypes();
        return new ResponseEntity<>(ticketTypes, HttpStatus.OK);
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
