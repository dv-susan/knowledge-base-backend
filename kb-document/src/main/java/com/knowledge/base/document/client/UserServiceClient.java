package com.knowledge.base.document.client;

import com.knowledge.base.common.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务客户端
 *
 * <p>通过 HTTP 调用 kb-user-auth 服务获取用户信息</p>
 */
@Slf4j
@Component
public class UserServiceClient {
    private static final String DEBUG_ENV_FILE = "/Users/mac/Documents/ai-test/cc/enterprise-knowledge-base/project/.dbg/category-auth-403.env";
    private static final String DEBUG_FALLBACK_URL = "http://127.0.0.1:7778/event";
    private static final String DEBUG_SESSION_ID = "category-auth-403";

    @Value("${kb-user-auth.url:http://localhost:8081}")
    private String userAuthUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取用户头像URL
     *
     * <p>从当前请求上下文中获取 JWT Token 并传递到 kb-user-auth，
     * 如果 Token 不可用（如异步线程），则不带认证头请求（降级为空头像）。</p>
     */
    public String getUserAvatar(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            String url = userAuthUrl + "/users/" + userId;
            HttpHeaders headers = new HttpHeaders();
            String token = UserContextUtil.getToken();
            if (token != null && !token.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION, buildBearerToken(token));
            }
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body != null && Integer.valueOf(200).equals(body.get("code"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null && data.get("avatar") != null) {
                    return data.get("avatar").toString();
                }
            }
        } catch (Exception e) {
            log.warn("获取用户头像失败: userId={}, error={}", userId, e.getMessage());
        }
        return null;
    }

    /**
     * 获取用户权限编码列表。
     *
     * @param userId 用户ID
     * @param token  当前请求携带的JWT Token
     * @return 权限编码列表，查询失败时返回空集合
     */
    public List<String> getUserPermissions(Long userId, String token) {
        if (userId == null || token == null || token.isBlank()) {
            // #region debug-point C:permission-short-circuit
            reportDebug("C", "UserServiceClient#getUserPermissions:73",
                    "[DEBUG] 权限拉取被短路",
                    Map.of(
                            "userId", userId,
                            "tokenPresent", token != null && !token.isBlank()
                    ));
            // #endregion
            return Collections.emptyList();
        }
        try {
            String url = userAuthUrl + "/users/" + userId + "/permissions";
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, buildBearerToken(token));
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body == null || !Integer.valueOf(200).equals(body.get("code"))) {
                return Collections.emptyList();
            }
            Object data = body.get("data");
            if (!(data instanceof List<?> rawList)) {
                return Collections.emptyList();
            }
            List<String> permissions = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                if (item != null) {
                    String permissionCode = item.toString().trim();
                    if (!permissionCode.isEmpty()) {
                        permissions.add(permissionCode);
                    }
                }
            }
            // #region debug-point C:permission-response
            reportDebug("C", "UserServiceClient#getUserPermissions:108",
                    "[DEBUG] 用户权限拉取结果",
                    Map.of(
                            "userId", userId,
                            "permissionCount", permissions.size(),
                            "permissions", permissions
                    ));
            // #endregion
            return permissions;
        } catch (Exception e) {
            // #region debug-point C:permission-error
            reportDebug("C", "UserServiceClient#getUserPermissions:117",
                    "[DEBUG] 用户权限拉取失败",
                    Map.of(
                            "userId", userId,
                            "error", e.getClass().getSimpleName() + ":" + e.getMessage()
                    ));
            // #endregion
            log.warn("获取用户权限失败: userId={}, error={}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 统一补齐 Bearer 前缀，避免下游认证失败。
     */
    private String buildBearerToken(String token) {
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    // #region debug-point shared:helpers
    private void reportDebug(String hypothesisId, String location, String msg, Map<String, Object> data) {
        try {
            String debugServerUrl = DEBUG_FALLBACK_URL;
            String sessionId = DEBUG_SESSION_ID;
            if (Files.exists(Path.of(DEBUG_ENV_FILE))) {
                String content = Files.readString(Path.of(DEBUG_ENV_FILE));
                for (String line : content.split("\\R")) {
                    if (line.startsWith("DEBUG_SERVER_URL=")) {
                        debugServerUrl = line.substring("DEBUG_SERVER_URL=".length());
                    } else if (line.startsWith("DEBUG_SESSION_ID=")) {
                        sessionId = line.substring("DEBUG_SESSION_ID=".length());
                    }
                }
            }
            Map<String, Object> payload = new HashMap<>();
            payload.put("sessionId", sessionId);
            payload.put("runId", "pre-fix");
            payload.put("hypothesisId", hypothesisId);
            payload.put("location", location);
            payload.put("msg", msg);
            payload.put("data", data);
            payload.put("ts", System.currentTimeMillis());
            restTemplate.postForEntity(debugServerUrl, payload, Void.class);
        } catch (RestClientException ignored) {
            // 调试埋点失败不能影响权限拉取逻辑
        } catch (Exception ignored) {
            // 调试埋点失败不能影响权限拉取逻辑
        }
    }
    // #endregion
}
