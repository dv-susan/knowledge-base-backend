package com.knowledge.base.document.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 字符编码过滤器
 *
 * <p>确保所有HTTP请求和响应都使用UTF-8编码，防止中文乱码问题</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = "/*")
public class CharacterEncodingFilter implements Filter {

    private static final String UTF_8 = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 设置请求编码为UTF-8
        if (httpRequest.getCharacterEncoding() == null) {
            httpRequest.setCharacterEncoding(UTF_8);
        }

        // 设置响应编码为UTF-8
        httpResponse.setCharacterEncoding(UTF_8);
        httpResponse.setContentType("application/json;charset=UTF-8");

        // 继续过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 销毁
    }
}