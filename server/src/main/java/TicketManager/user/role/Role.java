package TicketManager.user.role;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    USER_USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name(); // Добавим префикс 'ROLE_'
    }
}