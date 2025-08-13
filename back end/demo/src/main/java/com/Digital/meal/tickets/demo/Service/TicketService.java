package com.Digital.meal.tickets.demo.Service;


import com.Digital.meal.tickets.demo.Dto.CreateTicketRequest;
import com.Digital.meal.tickets.demo.Dto.TicketDTO;
import com.Digital.meal.tickets.demo.Entity.OrderEntity;
import com.Digital.meal.tickets.demo.Entity.Ott;
import com.Digital.meal.tickets.demo.Entity.Ticket;
import com.Digital.meal.tickets.demo.Entity.TicketTypeEntity;
import com.Digital.meal.tickets.demo.Repository.OrderRepository;
import com.Digital.meal.tickets.demo.Repository.OttRepository;
import com.Digital.meal.tickets.demo.Repository.TicketRepository;
import com.Digital.meal.tickets.demo.Repository.TicketTypeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OttRepository ottRepository;


    @Autowired
    private TicketTypeRepository ticketTypeRepository;


    @Autowired
    private TicketTypeService ticketTypeService;

    //    查询所有ticket表所有信息
    public List<TicketDTO> getselectAllTickets() {
        // 查询所有餐票实体
        List<Ticket> tickets = ticketRepository.findAll();
        // 转换为DTO并返回
        return tickets.stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    public TicketDTO getTicketById(String id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);
            return dto;
        }
        return null;
    }


    public List<TicketDTO> createTickets(CreateTicketRequest request) {
        String orderNumber = request.getOrderNumber();
        Integer orderAmount=request.getOrderAmount();

        // 根据订单编号查询订单
        Optional<OrderEntity> orderOptional = orderRepository.findByOrderNumber(orderNumber);
        if (!orderOptional.isPresent()) {
            logger.error("订单不存在，订单编号: {}", orderNumber);
            throw new RuntimeException("订单不存在");
        }
        OrderEntity order = orderOptional.get();

        List<Ott> orderItems = ottRepository.findByOrderId(order.getOrderId());
        if (orderItems.isEmpty() || orderItems.get(0).getTypeId() == null) {
            throw new RuntimeException("未找到订单关联的票种类型");
        }

        // 获取订单关联的票种类型
        String typeId = orderItems.get(0).getTypeId();
        TicketTypeEntity ticketType = ticketTypeRepository.findById(typeId).orElse(null);
        if (ticketType == null) {
            logger.error("票种类型不存在，类型ID: {}", typeId);
            throw new RuntimeException("票种类型不存在");
        }


        List<TicketDTO> tickets = new ArrayList<>();

        for (int i = 0; i < orderAmount; i++) {
            TicketDTO ticket = new TicketDTO();
            ticket.setTickerId(UUID.randomUUID().toString().substring(0, 10)); // 生成唯一的票ID
            ticket.setTypeID(typeId);
            ticket.setOrderId(order.getOrderId());
            ticket.setCanteenId(ticketType.getCanteenId());
            ticket.setCanteenName(ticketType.getTypeName()); // 假设食堂名字从票种类型的名称获取
            ticket.setStaus(String.valueOf(0)); // 初始状态为未使用
            ticket.setCreateDatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 优化日期设置
            ticket.setExpirationTime(String.valueOf(LocalDateTime.now().plusDays(7))); // 假设过期时间为7天后
            tickets.add(ticket);
        }


        // 保存餐票到数据库
        return tickets;
    }


//    @Transactional
//    public TicketDTO createTicket(TicketDTO ticketDto) {
//        Ticket ticket = new Ticket();
//        BeanUtils.copyProperties(ticketDto, ticket);
//        ticket.setCreateDatetime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//        ticket.setCreateIP("127.0.0.1");
//        ticket.setFk_Creator("system");
//        Ticket savedTicket = ticketRepository.save(ticket);
//        TicketDTO resultDto = new TicketDTO();
//        BeanUtils.copyProperties(savedTicket, resultDto);
//        return resultDto;
//    }


    public List<TicketDTO> queryTickets(String canteenId){
        List<Ticket> tickets = ticketRepository.findByStallId(canteenId);
        return tickets.stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean cancelTicket(String id, String checkId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            if (Integer.parseInt(ticket.getStaus()) == 0) {
                ticket.setStaus(String.valueOf(1));
                ticket.setCheckId(checkId);
                ticket.setCheckTime(new SimpleDateFormat("yyyyMMdd").format(new Date()));
                ticketRepository.save(ticket);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean batchCancelTickets(TicketDTO.BatchCancel batchCancel) {
        List<String> ticketId = batchCancel.getTicketId();
        if (ticketId == null || ticketId.isEmpty()) {
            return false;
        }
        List<Ticket> tickets = ticketRepository.findAllById(ticketId);
        String checkTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
        tickets.forEach(ticket -> {
            if (Integer.parseInt(ticket.getStaus()) == 0) {
                ticket.setStaus(String.valueOf(1));
                ticket.setCheckId(batchCancel.getCheckId());
                ticket.setCheckTime(checkTime);
            }
        });
        ticketRepository.saveAll(tickets);
        return true;
    }

    public byte[] generatePrintFile(String id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            String content = "餐票ID: " + ticket.getTickerId() + "\n" +
                    "类型ID: " + ticket.getTypeID() + "\n" +
                    "档口ID: " + ticket.getStallId() + "\n" +
                    "过期时间: " + ticket.getExpirationTime();
            return content.getBytes();
        }
        return null;
    }

    //    零点更新
    @Scheduled(cron = "0 0 0 * * ?")
    public void processExpiredTickets() {
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        List<Ticket> expiredTickets = ticketRepository.findByExpirationTimeLessThanAndStaus(currentDate, String.valueOf(0));
        expiredTickets.forEach(ticket -> {
            ticket.setStaus(String.valueOf(2));
        });
        ticketRepository.saveAll(expiredTickets);
        log.info("处理过期餐票，共处理 {} 张", expiredTickets.size());
    }

    public List<TicketTypeEntity> getTicketTypes() {
        return ticketTypeService.getAllTicketTypes(); // 调用服务类的方法
    }

    // 获取各食堂和档口的未核销/已核销餐票数量
    public List<Map<String, Object>> getVerifiedAndUnverifiedCounts() {
        return ticketRepository.countVerifiedAndUnverifiedByCanteenAndStall();
    }

    /// 按代理商汇总状态为0的餐票总数（去重agenciesId）
    public List<Map<String, Object>> getSumUnverifiedByAgency() {
        return ticketRepository.sumUnverifiedTicketsByAgency();
    }
}
