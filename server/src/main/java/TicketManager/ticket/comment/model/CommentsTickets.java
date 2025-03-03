package TicketManager.ticket.comment.model;

import TicketManager.ticket.model.Ticket;
import TicketManager.user.model.MyUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments_tickets")
public class CommentsTickets {
    @Id
    @Column(name = "comment_ticket_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private MyUser author;

    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticketId;

    @Column(name = "created")
    private LocalDateTime created;

    public CommentsTickets(MyUser author, String text, Ticket ticketId, LocalDateTime created) {
        this.author = author;
        this.text = text;
        this.ticketId = ticketId;
        this.created = created;
    }
}