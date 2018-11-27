package com.cash.overflow.gateway;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CorsFilter;

/**
 * Handle Cross Origin exception should be disable cross origin security as
 * {@link Ordered#HIGHEST_PRECEDENCE}, so,{@linkplain CorsFilter} will be
 * executed first than the other filter
 * 
 * @author kiditz
 */
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void init(FilterConfig fc) throws ServletException {
		log.info("Disable Cross Origin ");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;
		//Seperate By Comma http://0.0.0.0:8020, http://0.0.0.0:8021, http://0.0.0.0:8022, http://0.0.0.0:8090
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "PATCH,POST,GET,OPTIONS,DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, resp);
		}

	}

	@Override
	public void destroy() {
	}

}
