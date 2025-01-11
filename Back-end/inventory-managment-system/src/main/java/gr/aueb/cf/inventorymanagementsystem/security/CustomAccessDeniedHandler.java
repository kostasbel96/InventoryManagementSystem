package gr.aueb.cf.inventorymanagementsystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * When an authenticated user tries to access a resource without
 * the necessary permissions, Spring Security triggers the Access Denied Handler.
 * By default, it returns a 403 Forbidden status.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        // Set the response status and content type
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // Write a custom JSON response with the collected information
        String jsonResponse = "{\"code\": \"userNotAuthorized\", \"description\": \"User is not allowed to visit this route.\"}";
        response.getWriter().write(jsonResponse);
    }
}
