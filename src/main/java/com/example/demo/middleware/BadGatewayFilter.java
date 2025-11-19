package com.example.demo.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BadGatewayFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        chain.doFilter(request, response);

        // 如果沒有 handler 寫入 response，並且狀態碼是 404
        if (res.getStatus() == HttpStatus.NOT_FOUND.value()) {
            res.reset();
            res.setStatus(HttpStatus.BAD_GATEWAY.value());
            res.setContentType("application/json");
            res.getWriter().write(HttpStatus.BAD_GATEWAY.getReasonPhrase());
        }
    }
}

