package com.knowledge.base.common.exception;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>参考susan-mall-cloud的GlobalExceptionHandler实现</p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>统一处理所有业务服务的异常</li>
 *   <li>区分内部服务调用和外部API调用</li>
 *   <li>内部调用返回ResponseEntity（保留HTTP状态码）</li>
 *   <li>外部调用返回统一的Result格式（HTTP 200 + 业务错误码）</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一异常处理入口
     */
    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable e) {
        String requestInfo = getRequestInfo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 检查是否为内部服务调用
        if (Objects.nonNull(requestAttributes)) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (StringUtils.isNotEmpty(request.getHeader("INNER-REQUEST"))) {
                return handleInternalException(e, requestInfo);
            }
        }

        // 外部API调用
        return handleExternalException(e, requestInfo);
    }

    /**
     * 处理内部服务调用的异常
     * <p>内部服务调用返回ResponseEntity，保留HTTP状态码</p>
     */
    private Object handleInternalException(Throwable e, String requestInfo) {
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            log.error("内部调用业务异常：{} code={} msg={}", requestInfo, businessException.getCode(), businessException.getMessage(), e);
            return ResponseEntity.status(businessException.getCode()).body(businessException.getMessage());
        }
        log.error("内部调用异常：{}", requestInfo, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     * 处理外部API调用的异常
     * <p>外部API调用返回统一的Result格式，HTTP状态码统一为200</p>
     */
    private Object handleExternalException(Throwable e, String requestInfo) {
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            log.error("业务异常：{} code={} msg={}", requestInfo, businessException.getCode(), businessException.getMessage(), e);
            return Result.error(businessException.getCode(), businessException.getMessage());
        } else if (e instanceof AccessDeniedException) {
            log.warn("权限异常：{} msg={}", requestInfo, e.getMessage(), e);
            return Result.error(HttpStatus.FORBIDDEN.value(), "无权限访问，请联系系统管理员");
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            log.error("参数校验异常：{} {}", requestInfo, errorMsg);
            return Result.error(ResultCode.PARAM_ERROR.getCode(), errorMsg);
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            log.error("参数绑定异常：{} {}", requestInfo, errorMsg);
            return Result.error(ResultCode.PARAM_ERROR.getCode(), errorMsg);
        } else if (e instanceof IllegalArgumentException) {
            log.error("非法参数异常：{} {}", requestInfo, e.getMessage(), e);
            return Result.error(ResultCode.PARAM_ERROR.getCode(), e.getMessage());
        } else if (e instanceof IllegalStateException && e.getMessage() != null
                && (e.getMessage().contains("用户未登录") || e.getMessage().contains("用户登录信息不完整"))) {
            log.warn("用户认证异常：{} msg={}", requestInfo, e.getMessage());
            return Result.error(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        }

        log.error("系统异常：{} msg={}", requestInfo, e.getMessage(), e);
        return Result.error(ResultCode.ERROR);
    }

    /**
     * 获取请求信息
     */
    private static String getRequestInfo() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes) {
            HttpServletRequest req = ((ServletRequestAttributes) attrs).getRequest();
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String ip = req.getRemoteAddr();
            String fullUri = query == null ? uri : uri + "?" + query;
            return "method=" + method + " uri=" + fullUri + " ip=" + ip;
        }
        return "";
    }
}
