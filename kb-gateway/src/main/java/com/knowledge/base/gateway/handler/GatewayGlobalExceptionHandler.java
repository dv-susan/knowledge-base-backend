package com.knowledge.base.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.base.common.result.Result;
import io.netty.channel.ConnectTimeoutException;

import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 网关全局异常处理器
 *
 * <p>统一处理网关层和后端服务的异常，返回统一格式的错误响应</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Order(-1)
@Component("gatewayGlobalExceptionHandler")
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 根据异常类型生成错误响应
        Result<?> result;
        HttpStatus status;

        if (ex instanceof ResponseStatusException rse) {
            status = (HttpStatus) rse.getStatusCode();
            result = Result.error(status.value(), rse.getReason());
        } else if (ex instanceof ConnectTimeoutException || ex instanceof TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            result = Result.error(status.value(), "后端服务响应超时，请稍后重试");
        } else if (ex instanceof ConnectException) {
            status = HttpStatus.BAD_GATEWAY;
            result = Result.error(status.value(), "后端服务不可用，请检查服务状态");
        } else {
            log.error("网关异常: ", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result = Result.error(status.value(), "系统异常: " + ex.getMessage());
        }

        response.setStatusCode(status);

        try {
            String responseBody = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(responseBody.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return Mono.error(ex);
        }
    }
}
