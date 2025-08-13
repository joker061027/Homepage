package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.User;
import com.Digital.meal.tickets.demo.Service.UserService;
import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User save(@RequestBody User user) {
        // 验证并设置所有必填字段
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getRoleId() == null || user.getRoleId().trim().isEmpty()) {
            throw new RuntimeException("角色ID不能为空");
        }
        if (user.getAgenciesId() == null || user.getAgenciesId().trim().isEmpty()) {
            throw new RuntimeException("部门ID不能为空");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }

        // 设置系统字段
        if (user.getFkCreator() == null || user.getFkCreator().isEmpty()) {
            user.setFkCreator("system");
        }
        if (user.getCreateDatetime() == null || user.getCreateDatetime().isEmpty()) {
            user.setCreateDatetime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        }
        if (user.getCreateIP() == null || user.getCreateIP().isEmpty()) {
            user.setCreateIP("127.0.0.1");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber("00000000000"); // 如果没有提供手机号，设置为默认值
        }

        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable String id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return Result.success("获取用户信息成功", user.get());
        } else {
            return Result.error("用户不存在");
        }
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/page")
    public Page<User> findByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.findByPage(page, size);
    }

    /**
     * 获取用户详细信息（包含关联的角色和部门信息）
     */
    @GetMapping("/{id}/details")
    public Result<User> getUserWithDetails(@PathVariable String id) {
        Optional<User> user = userService.findByIdWithDetails(id);
        if (user.isPresent()) {
            return Result.success("获取用户详细信息成功", user.get());
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> getUserStatistics(@PathVariable String id) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取用户基本信息
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }

        User user = userOpt.get();
        statistics.put("userId", user.getUserId());
        statistics.put("userName", user.getUserName());
        statistics.put("fullName", user.getFullName());

        // 获取统计数据
        statistics.put("startedOrdersCount", userService.getStartedOrdersCount(id));
        statistics.put("approvedOrdersCount", userService.getApprovedOrdersCount(id));
        statistics.put("usedTicketsCount", userService.getUsedTicketsCount(id));

        return Result.success("获取用户统计信息成功", statistics);
    }
}