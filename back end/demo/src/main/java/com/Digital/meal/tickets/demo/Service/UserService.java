package com.Digital.meal.tickets.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Digital.meal.tickets.demo.Entity.User;
import com.Digital.meal.tickets.demo.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findByPage(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取用户详细信息（包含关联的角色和部门信息）
     */
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithDetails(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 触发懒加载，获取关联数据
            if (user.getRole() != null) {
                user.getRole().getRoleName(); // 触发角色信息加载
            }
            if (user.getAgencies() != null) {
                user.getAgencies().getAgenciesName(); // 触发部门信息加载
            }
        }
        return userOpt;
    }

    /**
     * 获取用户发起的订单数量
     */
    @Transactional(readOnly = true)
    public long getStartedOrdersCount(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getStartedOrders() != null ? user.getStartedOrders().size() : 0;
        }
        return 0;
    }

    /**
     * 获取用户审批的订单数量
     */
    @Transactional(readOnly = true)
    public long getApprovedOrdersCount(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getApprovedOrders() != null ? user.getApprovedOrders().size() : 0;
        }
        return 0;
    }

    /**
     * 获取用户使用的餐票数量
     */
    @Transactional(readOnly = true)
    public long getUsedTicketsCount(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getUsedTickets() != null ? user.getUsedTickets().size() : 0;
        }
        return 0;
    }
}