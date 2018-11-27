package com.overflow.cash;

import org.apache.http.HttpStatus;
import org.slerp.core.Domain;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
@Component
public class RestAuthenticationFailureHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        Domain domain = new Domain();
        domain.put("status", "FAIL");
        domain.put("message", e.getMessage());
        domain.put("path", req.getRequestURL().toString());
        res.setStatus(HttpStatus.SC_OK);
        PrintWriter writer = res.getWriter();
        writer.write(domain.toString());
        writer.flush();
    }
}
