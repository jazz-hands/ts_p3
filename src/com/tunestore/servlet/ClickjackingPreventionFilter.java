package com.tunestore.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


public class ClickjackingPreventionFilter implements Filter {

    private String mode = "DENY";

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse)response;
        res.addHeader("X-FRAME-OPTIONS", mode );   
        chain.doFilter(request, response);
    }

	public void init(FilterConfig fConfig) throws ServletException {
        String configMode = fConfig.getInitParameter("mode");
        if ( configMode != null ) {
            mode = configMode;
        }
    }

}
