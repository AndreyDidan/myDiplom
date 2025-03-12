package TicketManager.ticket.recomendations.work.model;

import TicketManager.ticket.model.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "correction_recomendations_tickets")
public class RecomendationsWork {

    @Id
    @Column(name = "recomendation_work_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticketId;

    @Column(name = "recomendation_work")
    private String recomendationWork;

    public RecomendationsWork(Ticket ticketId, String recomendationWork) {
        this.ticketId = ticketId;
        this.recomendationWork = recomendationWork;
    }
}
