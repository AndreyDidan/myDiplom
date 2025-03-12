package TicketManager.ticket.recomendations.work.repository;

import TicketManager.ticket.model.Ticket;
import TicketManager.ticket.recomendations.work.model.RecomendationsWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecomendationsWorkRepository extends JpaRepository<RecomendationsWork, Long> {
    // Другие методы для поиска рекомендаций
    List<RecomendationsWork> findByTicketId(Ticket ticket);
}
