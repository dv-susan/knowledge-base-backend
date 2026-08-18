package com.knowledge.base.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 *
 * <p>提供安全相关的工具方法</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class SecurityUtil {

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取用户代理
     *
     * @param request HttpServletRequest
     * @return 用户代理
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 检查是否为移动设备
     *
     * @param request HttpServletRequest
     * @return 是否为移动设备
     */
    public static boolean isMobileDevice(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return false;
        }

        return userAgent.toLowerCase().matches(".*(android|iphone|ipad|ipod|windows phone|mobile).*");
    }

    /**
     * 获取浏览器类型
     *
     * @param request HttpServletRequest
     * @return 浏览器类型
     */
    public static String getBrowserType(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }

    /**
     * 获取操作系统
     *
     * @param request HttpServletRequest
     * @return 操作系统
     */
    public static String getOperatingSystem(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "MacOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    /**
     * HTML转义
     *
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    /**
     * SQL注入检查
     *
     * @param input 输入字符串
     * @return 是否包含SQL注入
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }

        String[] sqlKeywords = {
                "select", "insert", "update", "delete", "drop", "union",
                "exec", "execute", "script", "javascript", "alert",
                "--", "/*", "*/", ";", "'", "\"", "=", "or", "and"
        };

        String lowerInput = input.toLowerCase();
        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * XSS攻击检查
     *
     * @param input 输入字符串
     * @return 是否包含XSS攻击
     */
    public static boolean containsXssAttack(String input) {
        if (input == null) {
            return false;
        }

        String[] xssPatterns = {
                "<script", "</script>", "javascript:", "onerror=", "onload=",
                "onclick=", "onmouseover=", "onfocus=", "onblur=",
                "<iframe", "</iframe>", "<object", "</object>", "<embed"
        };

        String lowerInput = input.toLowerCase();
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 清理输入字符串
     *
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // 移除危险字符
        return input.replaceAll("[<>\"'']", "")
                     .replaceAll("[/\\\\*]", "")
                     .trim();
    }
}
