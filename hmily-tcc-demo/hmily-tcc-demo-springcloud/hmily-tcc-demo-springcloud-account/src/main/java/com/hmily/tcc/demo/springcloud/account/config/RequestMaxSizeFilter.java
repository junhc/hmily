package com.hmily.tcc.demo.springcloud.account.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Name: RequestMaxSizeFilter
 * Function:
 *
 * @Author: K.K
 * Create Time: 2025/6/14 19:12
 * Modified By:
 * Modified Time:
 * Description:
 * Version:
 */
public class RequestMaxSizeFilter implements Filter {
    private final Long maxSize;

    public RequestMaxSizeFilter(Long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) || "PUT".equalsIgnoreCase(httpRequest.getMethod())) {
            long contentLength = httpRequest.getContentLengthLong();
            if (contentLength > maxSize) {
                httpResponse.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Request body exceeds maximum size of " + maxSize);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
