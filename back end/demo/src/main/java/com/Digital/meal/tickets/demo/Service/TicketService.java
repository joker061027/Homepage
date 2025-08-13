package com.Digital.meal.tickets.demo.Service;


import com.Digital.meal.tickets.demo.Dto.TicketDTO;
import com.Digital.meal.tickets.demo.Entity.*;
import com.Digital.meal.tickets.demo.Repository.OrderRepository;
import com.Digital.meal.tickets.demo.Repository.OttRepository;
import com.Digital.meal.tickets.demo.Repository.TicketRepository;
import com.Digital.meal.tickets.demo.Repository.TicketTypeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    // 查询全部餐票（包含票种类型和食堂信息）
    public List<TicketDTO> getselectAllTickets() {
        // 调用三级关联查询
        List<Ticket> ticketEntities = ticketRepository.findAllWithTicketTypeAndCanteen();
        return ticketEntities.stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);

            // 从关联关系中提取食堂信息（层层校验，避免空指针）
            TicketTypeEntity ticketType = ticket.getTicketType();
            if (ticketType != null) {
                Canteen canteen = ticketType.getCanteen();
                if (canteen != null) {
                    dto.setCanteenId(canteen.getCanteenId()); // 食堂ID
                    dto.setCanteenName(canteen.getCanteenName()); // 从食堂表获取名称
                } else {
                    dto.setCanteenId(ticketType.getCanteenId()); // 至少保留票种类型中的canteenId
                    dto.setCanteenName("未知食堂（食堂表无记录）");
                }
            }
            else {
                dto.setCanteenId("未知食堂ID");
                dto.setCanteenName("未知食堂（票种类型无记录）");
            }
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



    // 根据id查询餐票信息
    public TicketDTO getTicketById(String id) {
        // 使用带关联查询的方法获取餐票信息
        Optional<Ticket> optionalTicket = ticketRepository.findByIdWithTicketTypeAndCanteen(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);

            // 从关联关系中提取食堂信息（避免空指针）
            TicketTypeEntity ticketType = ticket.getTicketType();
            if (ticketType != null) {
                dto.setCanteenId(ticketType.getCanteenId()); // 设置食堂ID

                Canteen canteen = ticketType.getCanteen();
                if (canteen != null) {
                    dto.setCanteenName(canteen.getCanteenName()); // 从食堂表获取名称
                } else {
                    dto.setCanteenName("未知食堂（食堂表无记录）");
                }
            } else {
                dto.setCanteenId("未知食堂ID");
                dto.setCanteenName("未知食堂（票种类型无记录）");
            }
            return dto;
        }
        return null;
    }




    /**
     * 根据订单编号和数量创建一批餐票
     */
    @Transactional
    public List<TicketDTO> createTickets(TicketDTO.CreateTicketRequest request, HttpServletRequest httpRequest) {
        // 1. 根据订单编号查询订单信息
        Optional<OrderEntity> orderOptional = orderRepository.findByOrderNumber(request.getOrderNumber());
        if (!orderOptional.isPresent()) {
            log.error("订单不存在，订单编号: {}", request.getOrderNumber());
            throw new RuntimeException("订单不存在");
        }
        OrderEntity order = orderOptional.get();

        // 2. 获取订单关联的票种类型信息
        List<Ott> orderItems = ottRepository.findByOrderId(order.getOrderId());
        if (orderItems.isEmpty() || orderItems.get(0).getTypeId() == null) {
            throw new RuntimeException("未找到订单关联的票种类型");
        }
        String typeId = orderItems.get(0).getTypeId();
        TicketTypeEntity ticketType = ticketTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("票种类型不存在，类型ID: " + typeId));

        // 3. 生成餐票列表
        List<Ticket> tickets = new ArrayList<>();
        String orderNumber = request.getOrderNumber();
        String createIp = getClientIp(httpRequest);
        String createDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        for (int i = 0; i < request.getQuantity(); i++) {
            Ticket ticket = new Ticket();

            // 使用雪花算法生成餐票ID（这里使用简化版实现，实际项目建议使用成熟的雪花算法工具类）
            ticket.setTickerId(generateSnowflakeId());

            // 生成餐票编号：订单编号 + 4位序号（从1开始）
            String ticketNumber = orderNumber + String.format("%04d", i + 1);
            ticket.setTicketNumber(ticketNumber);

            // 设置其他属性
            ticket.setTypeID(typeId);
            ticket.setTypename(ticketType.getTypeName());
            ticket.setOrderId(order.getOrderId());
            ticket.setExpirationTime(request.getExpirationTime());
            ticket.setStaus(request.getStatus());
            ticket.setFk_Creator(request.getFkCreator());
            ticket.setCreateDatetime(createDateTime);
            ticket.setCreateIP(createIp);

            tickets.add(ticket);
        }

        // 4. 保存餐票
        List<Ticket> savedTickets = ticketRepository.saveAll(tickets);

        // 5. 转换为DTO并补充食堂信息
        return savedTickets.stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);

            // 从已获取的ticketType中提取食堂信息（无需再次查询数据库）
            dto.setCanteenId(ticketType.getCanteenId()); // 设置食堂ID

            // 获取食堂名称（从ticketType关联的食堂实体）
            Canteen canteen = ticketType.getCanteen();
            dto.setCanteenName(canteen != null ? canteen.getCanteenName() : "未知食堂");

            return dto;
        }).collect(Collectors.toList());
    }
    /**
     * 简化版雪花算法ID生成（实际项目建议使用成熟实现）
     */
    private String generateSnowflakeId() {
        // 这里使用时间戳+随机数模拟雪花算法
        return "TK" + System.currentTimeMillis() + RandomUtils.nextInt(1000, 9999);
    }
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
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
        // 对于通过多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    public List<TicketDTO> queryTickets(String canteenId){
        List<Ticket> tickets = ticketRepository.findByStallId(canteenId);
        return tickets.stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto);
            return dto;
        }).collect(Collectors.toList());
    }



    /**
     * 单个核销餐票
     */
    @Transactional
    public boolean verifyTicket(String ticketId, TicketDTO.VerifyTicket verifyInfo) {
        // 查询餐票是否存在
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            // 仅当状态为3时允许核销（3表示可核销状态）
            if ("3".equals(ticket.getStaus())) {
                // 更新状态为99（已核销）
                ticket.setStaus("99");
                // 仅设置核销人ID（checkId）
                ticket.setCheckId(verifyInfo.getCheckId());
                // 设置核销时间（格式与现有代码保持一致：yyyyMMdd）
                String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
                ticket.setCheckTime(currentTime);

                // 保存更新（不修改stallId、usedId、useTime等字段）
                ticketRepository.save(ticket);
                return true;
            }
        }
        return false;
    }

    /**
     * 批量核销餐票
     * @param batchVerify 批量核销参数（核销人）
     * @return 核销成功的数量
     */
    @Transactional
    public int batchVerifyTickets(TicketDTO.BatchVerify batchVerify) {
        if (batchVerify.getTicketIds() == null || batchVerify.getTicketIds().isEmpty()) {
            return 0;
        }

        List<Ticket> tickets = ticketRepository.findAllById(batchVerify.getTicketIds());
        int successCount = 0;
        String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date()); // 统一核销时间

        for (Ticket ticket : tickets) {
            if ("3".equals(ticket.getStaus())) {
                ticket.setStaus("99"); // 更新为已核销状态
                ticket.setCheckId(batchVerify.getCheckId()); // 核销人ID
                ticket.setCheckTime(currentTime); // 核销时间
                successCount++;
            }
        }
        // 批量保存更新
        if (successCount > 0) {
            ticketRepository.saveAll(tickets);
        }
        return successCount;
    }



    /**
     * 检查餐票（根据编号查询）
     */
    public TicketDTO checkTicket(String ticketNumber) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            TicketDTO dto = new TicketDTO();
            BeanUtils.copyProperties(ticket, dto); // 复制基础字段

            // 补充票种类型信息（typename等）
            TicketTypeEntity ticketType = ticket.getTicketType();
            if (ticketType != null) {
                dto.setTypename(ticketType.getTypeName()); // 设置票种名称
                dto.setCanteenId(ticketType.getCanteenId()); // 设置食堂ID

                // 补充食堂名称
                Canteen canteen = ticketType.getCanteen();
                if (canteen != null) {
                    dto.setCanteenName(canteen.getCanteenName());
                } else {
                    dto.setCanteenName("未知食堂（食堂表无记录）");
                }
            } else {
                dto.setTypename("未知票种");
                dto.setCanteenId("未知食堂ID");
                dto.setCanteenName("未知食堂（票种类型无记录）");
            }

            return dto;
        }
        return null; // 餐票不存在
    }


    /**
     * 使用餐票（更新为已使用状态）
     */
    @Transactional
    public TicketDTO useTicket(String ticketId, String stallId, String usedId) {
        // 查询餐票是否存在
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            // 检查当前状态是否可使用（根据业务定义，0为未使用等）
            if ("0".equals(ticket.getStaus()) && !isExpired(ticket.getExpirationTime())) {
                ticket.setStaus("3");
                ticket.setStallId(stallId);
                ticket.setUsedId(usedId);
                String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
                ticket.setUseTime(currentTime);
                Ticket savedTicket = ticketRepository.save(ticket);
                TicketDTO dto = new TicketDTO();
                BeanUtils.copyProperties(savedTicket, dto);
                return dto;
            }
        }
        return null;
    }
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
     * 获取已核销的餐票（状态为99）
     * @return 包含餐票列表和数量的Map
     */
    public Map<String, Object> getVerifiedTickets() {
        List<Ticket> tickets = ticketRepository.findByStaus("99");
        long count = ticketRepository.countByStaus("99");

        Map<String, Object> result = new HashMap<>();
        result.put("tickets", tickets);
        result.put("count", count);
        return result;
    }

    /**
     * 获取未核销的餐票（状态为0）
     * @return 包含餐票列表和数量的Map
     */
    public Map<String, Object> getUnverifiedTickets() {
        List<Ticket> tickets = ticketRepository.findByStaus("0");
        long count = ticketRepository.countByStaus("0");

        Map<String, Object> result = new HashMap<>();
        result.put("tickets", tickets);
        result.put("count", count);
        return result;
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
