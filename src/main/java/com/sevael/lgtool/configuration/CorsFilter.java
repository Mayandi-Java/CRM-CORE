package com.sevael.lgtool.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		    HttpServletResponse response = (HttpServletResponse) servletResponse;
	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
	        response.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Max-Age", "180");
	        filterChain.doFilter(servletRequest, servletResponse);
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
