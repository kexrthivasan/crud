package com.keerthivasan.crud.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String path = request.getRequestURI();
        String method = request.getMethod();

        String message;

        if (path.matches(".*/api/students/\\d+$") && method.equals("DELETE")) {
            message = "Access Denied - Only Admin can delete student records.";
        } else if (path.matches(".*/api/students/\\d+$") && method.equals("PUT")) {
            message = "Access Denied - Only Admin can update student records.";
        } else {
            message = "Access Denied - You do not have permission to access this resource.";
        }

        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
