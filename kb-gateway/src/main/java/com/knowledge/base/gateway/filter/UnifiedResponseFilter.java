package com.knowledge.base.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 统一响应过滤器
 *
 * <p>参考susan-mall-cloud的AuthFilter实现，统一处理响应体封装</p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>拦截后端服务响应，确保响应格式统一</li>
 *   <li>处理分段传输的数据</li>
 *   <li>自动包装非标准格式的响应</li>
 *   <li>记录完整的响应日志</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class UnifiedResponseFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    String contentType = getDelegate().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

                    // 只处理JSON格式的响应
                    if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        // 处理分段传输的数据
                        return super.writeWith(fluxBody.buffer().flatMap(dataBuffers -> {
                            String responseData;
                            try {
                                // 先合并所有DataBuffer的字节（避免分段解码导致UTF-8多字节字符被截断产生乱码）
                                int totalSize = 0;
                                for (DataBuffer dataBuffer : dataBuffers) {
                                    totalSize += dataBuffer.readableByteCount();
                                }
                                byte[] allBytes = new byte[totalSize];
                                int offset = 0;
                                for (DataBuffer dataBuffer : dataBuffers) {
                                    int size = dataBuffer.readableByteCount();
                                    dataBuffer.read(allBytes, offset, size);
                                    offset += size;
                                }
                                // 一次性解码所有字节，保证UTF-8多字节字符（如中文）的完整性
                                responseData = new String(allBytes, StandardCharsets.UTF_8);
                            } catch (Exception e) {
                                log.error("读取响应字节流异常：{}", e.getMessage(), e);
                                responseData = "";
                            }

                            // 释放原始数据缓冲区
                            dataBuffers.forEach(DataBufferUtils::release);
                            log.info("网关转发响应: URI={}, Status={}, Response={}",
                                    exchange.getRequest().getURI(),
                                    getStatusCode(),
                                    responseData);

                            // 包装响应数据
                            String wrappedResponse = wrapResponse(responseData);
                            byte[] uppedContent = wrappedResponse.getBytes(StandardCharsets.UTF_8);

                            // 设置Content-Type，确保UTF-8编码
                            getDelegate().getHeaders().setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
                            // 更新Content-Length
                            getDelegate().getHeaders().setContentLength(uppedContent.length);

                            // 返回新的数据缓冲区
                            return Mono.just(bufferFactory.wrap(uppedContent));
                        }));
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 包装响应数据
     * <p>如果响应数据已经是标准格式，则直接返回；否则包装为标准格式</p>
     *
     * @param responseData 原始响应数据
     * @return 包装后的响应数据
     */
    private String wrapResponse(String responseData) {
        try {
            // 尝试解析为JSON
            Object json = JSON.parse(responseData);
            if (json instanceof JSONObject) {
                JSONObject obj = (JSONObject) json;
                // 检查是否已经包含code和message字段（标准Result格式）
                if (obj.containsKey("code") && obj.containsKey("message")) {
                    return responseData;
                }
            }
        } catch (Exception ignored) {
            // JSON解析失败，说明不是标准格式，需要包装
        }

        // 包装为标准Result格式
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", JSON.parse(responseData));
        result.put("timestamp", System.currentTimeMillis());

        return result.toJSONString();
    }

    @Override
    public int getOrder() {
        return -2; // 设置较高优先级
    }
}
