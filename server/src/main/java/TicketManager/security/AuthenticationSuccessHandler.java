package TicketManager.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
        boolean isUserUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER_USER"));

        if (isAdmin) {
            setDefaultTargetUrl("/");  // Перенаправление для администратора
        } else if (isUser) {
            setDefaultTargetUrl("/");  // Перенаправление для пользователей с ролью "USER"
        } else if (isUserUser) {
            setDefaultTargetUrl("/problem/all");  // Перенаправление для пользователей с ролью "USER_USER"
        } else {
            setDefaultTargetUrl("/login?error=true");  // Если роли не совпадают
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}