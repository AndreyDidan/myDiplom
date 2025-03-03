package TicketManager.ticket.comment.repository;

import TicketManager.ticket.comment.model.CommentsTickets;
import TicketManager.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentsTickets, Long> {
    List<CommentsTickets> findAll();
    List<CommentsTickets> findByTicketIdOrderByCreatedAsc(Ticket ticketId);
}
