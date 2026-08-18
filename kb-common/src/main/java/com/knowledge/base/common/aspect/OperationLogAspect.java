package com.knowledge.base.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.config.InstanceIdentifier;
import com.knowledge.base.common.event.OperationLogEventDTO;
import com.knowledge.base.common.utils.UserContextUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 操作日志 AOP 切面
 *
 * <p>拦截 @OperationLog 注解的方法，记录操作日志并通过 RabbitMQ 异步发布，
 * 由 kb-foundation 服务消费后写入 kb_operation_log 表</p>
 *
 * <p>仅在 Servlet Web 环境下生效，WebFlux（如 Gateway）中自动禁用</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OperationLogAspect {

    private static final String OPERATION_LOG_EXCHANGE = "kb.operationlog.exchange";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private HttpServletRequest request;

    @Resource
    private InstanceIdentifier instanceIdentifier;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLogEventDTO event = buildBaseEvent(joinPoint, operationLog);

        try {
            Object result = joinPoint.proceed();
            long executeTime = System.currentTimeMillis() - startTime;

            event.setExecuteTime((int) executeTime);
            event.setStatus(1);

            publishEvent(event);
            log.debug("操作日志记录成功：module={}, operation={}, executeTime={}ms",
                    event.getModule(), event.getOperationType(), executeTime);

            return result;
        } catch (Throwable e) {
            long executeTime = System.currentTimeMillis() - startTime;

            event.setExecuteTime((int) executeTime);
            event.setStatus(0);
            event.setErrorMsg(truncate(e.getMessage(), 500));

            publishEvent(event);
            log.warn("操作日志记录（失败）：module={}, operation={}, executeTime={}ms, error={}",
                    event.getModule(), event.getOperationType(), executeTime, e.getMessage());

            throw e;
        }
    }

    /**
     * 构建基础事件对象
     */
    private OperationLogEventDTO buildBaseEvent(ProceedingJoinPoint joinPoint, OperationLog operationLog) {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURI();

        // 序列化请求参数（过滤掉文件流等不可序列化类型，避免 OOM）
        String requestParams = null;
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                requestParams = JSON.toJSONString(filterArgs(args));
            }
        } catch (Exception e) {
            requestParams = "[序列化失败]";
        }

        // 获取用户信息
        Long userId = UserContextUtil.getUserId();
        String username = UserContextUtil.getUsername();

        // 获取客户端信息
        String ipAddress = getClientIp();
        String userAgent = request.getHeader("User-Agent");

        return OperationLogEventDTO.builder()
                .module(operationLog.module())
                .operationType(operationLog.operation())
                .operationDesc(operationLog.description())
                .requestMethod(requestMethod)
                .requestUrl(requestUrl)
                .requestParams(truncate(requestParams, 1000))
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(truncate(userAgent, 500))
                .build();
    }

    /**
     * 异步发布事件到 RabbitMQ
     * <p>直接 JSON 序列化后通过 send() 发送，显式指定 ContentType 为 application/json，
     * 绕过 RabbitTemplate 默认的 Java 序列化（SimpleMessageConverter），
     * 避免跨服务 MessageConverter 配置不一致导致的序列化/反序列化异常。</p>
     */
    private void publishEvent(OperationLogEventDTO event) {
        try {
            String json = JSON.toJSONString(event);
            byte[] body = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String routingKey = "operationlog." + instanceIdentifier.getId() + ".record";
            rabbitTemplate.send(OPERATION_LOG_EXCHANGE, routingKey,
                    new org.springframework.amqp.core.Message(body,
                            new org.springframework.amqp.core.MessageProperties() {{
                                setContentType("application/json");
                                setHeader("__TypeId__", "com.knowledge.base.common.event.OperationLogEventDTO");
                            }}));
        } catch (Exception e) {
            log.error("发布操作日志事件失败：module={}, operation={}, error={}",
                    event.getModule(), event.getOperationType(), e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 过滤方法参数，替换不可序列化类型为轻量描述符，避免大文件OOM
     */
    private Object[] filterArgs(Object[] args) {
        Object[] filtered = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            filtered[i] = filterArg(args[i]);
        }
        return filtered;
    }

    private Object filterArg(Object arg) {
        if (arg == null) return null;
        if (arg instanceof MultipartFile f) {
            return Map.of(
                    "type", "MultipartFile",
                    "originalFilename", f.getOriginalFilename() != null ? f.getOriginalFilename() : "unknown",
                    "size", f.getSize()
            );
        }
        if (arg instanceof HttpServletRequest) return "[HttpServletRequest]";
        if (arg instanceof HttpServletResponse) return "[HttpServletResponse]";
        if (arg instanceof InputStream) return "[InputStream]";
        if (arg instanceof OutputStream) return "[OutputStream]";
        return arg;
    }

    /**
     * 截断字符串到指定长度
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
