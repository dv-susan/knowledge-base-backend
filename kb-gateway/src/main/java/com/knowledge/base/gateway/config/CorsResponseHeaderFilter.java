package com.knowledge.base.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CORS响应头去重过滤器
 *
 * <p>参考susan-mall-cloud项目的CORS解决方案</p>
 * <p>去除后端服务设置的重复CORS头，确保只有网关层设置的CORS头返回给客户端</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

    private static final String ANY = "*";

    @Override
    public int getOrder() {
        // 在NettyWriteResponseFilter之后执行，确保在响应头处理完后执行去重
        return org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            try {
                if (exchange.getResponse().isCommitted()) {
                    return;
                }
                HttpHeaders headers = exchange.getResponse().getHeaders();
                if (headers == null || headers.isEmpty()) {
                    return;
                }

                // 使用更安全的方式遍历headers，避免Reactor环境中的NullPointerException
                // 直接操作特定的CORS头，而不是遍历整个entrySet
                deduplicateCorsHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                deduplicateCorsHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                deduplicateCorsHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                deduplicateCorsHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                deduplicateVaryHeader(headers);

            } catch (Exception e) {
                log.error("CORS响应头去重处理发生异常", e);
            }
        }));
    }

    /**
     * 去重CORS头
     *
     * @param headers HTTP响应头
     * @param headerName 要去重的头名称
     */
    private void deduplicateCorsHeader(HttpHeaders headers, String headerName) {
        try {
            List<String> values = headers.get(headerName);
            if (values == null || values.size() <= 1) {
                return;
            }

            List<String> deduplicatedValues = new ArrayList<>();
            // Access-Control-Allow-Origin 不能为 * 当 Access-Control-Allow-Credentials 为 true
            // 如果有多个值，优先保留非 * 的具体 origin
            if (headerName.equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                String nonAny = values.stream().filter(v -> !ANY.equals(v)).findFirst().orElse(ANY);
                deduplicatedValues.add(nonAny);
            } else if (values.contains(ANY)) {
                deduplicatedValues.add(ANY);
            } else {
                deduplicatedValues.add(values.get(0));
            }

            headers.put(headerName, deduplicatedValues);
            log.debug("CORS头去重处理: {} -> {}", headerName, deduplicatedValues);
        } catch (Exception e) {
            log.warn("处理CORS头时发生异常: headerName={}, error={}", headerName, e.getMessage());
        }
    }

    /**
     * 去重Vary头
     *
     * @param headers HTTP响应头
     */
    private void deduplicateVaryHeader(HttpHeaders headers) {
        try {
            List<String> varyValues = headers.get(HttpHeaders.VARY);
            if (varyValues == null || varyValues.size() <= 1) {
                return;
            }

            List<String> deduplicatedValues = varyValues.stream()
                    .distinct()
                    .collect(Collectors.toList());

            headers.put(HttpHeaders.VARY, deduplicatedValues);
            log.debug("Vary头去重处理: {} -> {}", HttpHeaders.VARY, deduplicatedValues);
        } catch (Exception e) {
            log.warn("处理Vary头时发生异常: error={}", e.getMessage());
        }
    }
}
