package org.example.plastinka2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.error("Resource not found error: ", ex);
        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Ресурс не найден");
            response.put("message", ex.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("error/404");
    }

    // Остальные обработчики остаются без изменений
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied error: ", ex);
        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Доступ запрещен");
            response.put("message", ex.getMessage());
            response.put("status", HttpStatus.FORBIDDEN.value());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        return new ModelAndView("error/403");
    }

    // ... остальные методы обработчиков


    @ExceptionHandler(UsernameNotFoundException.class)
    public Object handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        logger.error("User not found error: ", ex);
        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Пользователь не найден");
            response.put("message", ex.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("error/404");
    }

    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        logger.error("Runtime error: ", ex);
        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Внутренняя ошибка сервера");
            response.put("message", ex.getMessage());
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ModelAndView("error/500");
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error: ", ex);
        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Внутренняя ошибка сервера");
            response.put("message", "Произошла непредвиденная ошибка");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ModelAndView("error/500");
    }
} 