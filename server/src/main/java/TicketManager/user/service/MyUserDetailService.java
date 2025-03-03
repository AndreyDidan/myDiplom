package TicketManager.user.service;

import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private MyUserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.info("Попытка аутентификации с логином: " + login);
        Optional<MyUser> user = repository.findByLogin(login);
        if (user.isEmpty()) {
            log.error("Пользователь с логином {} не найден", login);
            throw new UsernameNotFoundException("Пользователь с логином " + login + " не найден");
        }
        log.info("Пользователь {} успешно загружен", login);
        return user.get();
    }
}