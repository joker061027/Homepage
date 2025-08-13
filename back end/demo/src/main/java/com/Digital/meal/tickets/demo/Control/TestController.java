package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Common.Result;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * 测试控制器 - 用于验证应用基本功能
 */
@RestController
@RequestMapping("/test")

public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello! Digital Meal Tickets API is working!");
    }

    @GetMapping("/time")
    public Result<String> currentTime() {
        return Result.success("Current time: " + LocalDateTime.now());
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Application is healthy and running!");
    }
}
