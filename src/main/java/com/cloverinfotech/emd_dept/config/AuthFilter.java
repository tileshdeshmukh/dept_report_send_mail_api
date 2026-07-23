package com.cloverinfotech.emd_dept.config;

import com.cloverinfotech.emd_dept.modal.LoginToken;
import com.cloverinfotech.emd_dept.repository.LoginTokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Autowired
    private LoginTokenRepository loginTokenRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        
        boolean isApiRequest = path.startsWith("/api/");
        boolean isPublicApi = path.startsWith("/api/user/login") || path.startsWith("/api/user/userRegistor");


        // Allow these WITHOUT a token
//        if (path.startsWith("/api/user/login") || path.startsWith("/api/user/userRegistor")) {
//            chain.doFilter(req, res);
//            return;
//        }
        if (!isApiRequest || isPublicApi) {
            chain.doFilter(req, res);
            return;
        }

     
        String token = request.getHeader("Authorization");

        boolean isValid = false;

        if (token != null) {
            LoginToken loginToken = loginTokenRepository.findByToken(token).orElse(null);
            if (loginToken != null && loginToken.isActive()) {
                isValid = true;
            }
        }

        if (!isValid) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"statusCode\":401,\"status\":\"FAILED\",\"message\":\"Unauthorized: please login first\"}"
            );
            return;  
        }

        chain.doFilter(req, res);   // Token valid — let request continue to Controller
    }
}