package com.offnal.shifterz.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        if (authException.getCause() instanceof com.offnal.shifterz.jwt.exception.JwtAuthException jwtAuthException) {
            var reason = jwtAuthException.getErrorReason();
            response.setStatus(reason.getStatus().value());
            response.getWriter().write("""
            {
              "code": "%s",
              "message": "%s"
            }
            """.formatted(reason.getCode(), reason.getMessage()));
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("""
            {
              "code": "JWT000",
              "message": "%s"
            }
            """.formatted(authException.getMessage()));
        }
    }
}
