package TicketManager.ticket.repository;

import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAll();
    Optional<Ticket> findByDescription(String description);
    Optional<Ticket> findByLittleDescription(String littleDescription);
    List<Ticket> findByIsExpiredTrue();

    List<Ticket> findByNameFunction(String nameFunction); // Найти билеты по функции
}