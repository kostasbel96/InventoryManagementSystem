package gr.aueb.cf.inventorymanagementsystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * When an unauthenticated user tries to access a secured endpoint,
 * the Authentication Entry Point is triggered. By default, in a web application,
 * this usually redirects the user to a login page or returns a 401 Unauthorized
 * status for APIs.
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Set the response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Write a custom error message or JSON structure
        //String json = String.format("{\"message\": \"%s\", \"error\": \"Unauthorized\"}", authException.getMessage());
        String json = "{\"code\": \"userNotAuthenticated\", \"description\": \"User needs to authenticate in order to access this route\"}";
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
