package com.Digital.meal.tickets.demo.Common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * 全局异常处理器
 * 统一处理所有控制器抛出的异常，避免重复的try-catch代码
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Result.error("400", e.getMessage()));
    }

    /**
     * 处理Bean验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<String>> handleValidationException(MethodArgumentNotValidException e) {
        String message = "参数验证失败";
        if (e.getBindingResult() != null) {
            var fieldError = e.getBindingResult().getFieldError();
            if (fieldError != null) {
                String fieldErrorMessage = fieldError.getDefaultMessage();
                if (fieldErrorMessage != null) {
                    message = fieldErrorMessage;
                }
            }
        }
        return ResponseEntity.badRequest().body(Result.error("400", message));
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<String>> handleRuntimeException(RuntimeException e) {
        if (e.getMessage() != null && e.getMessage().contains("不存在")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.error("404", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error("500", "服务器内部错误: " + e.getMessage()));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<String>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error("500", "系统异常: " + e.getMessage()));
    }
}
