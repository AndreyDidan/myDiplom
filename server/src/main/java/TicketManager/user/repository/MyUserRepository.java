package TicketManager.user.repository;

import TicketManager.user.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByLogin(String login);
    List<MyUser> findAll();
}