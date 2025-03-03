package TicketManager.ticket.SolutionInClose.model;

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
@Table(name = "solutions_in_close")
public class SolutionInClose {
    @Id
    @Column(name = "solutions_in_close_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_customer_id")
    private MyUser userCustomerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_executor_id")
    private MyUser userExecutorId;

    @Column(name = "solution_in_reasons")
    private String solutionInReasons;

    @Column(name = "solution_in_correction")
    private String solutionInCorrection;

    @Column(name = "answer_good")
    private Boolean answerGood;

    @Column(name = "created_solution")
    private LocalDateTime created;

    public SolutionInClose(Ticket ticket, MyUser userCustomerId, MyUser userExecutorId, String solutionInReasons,
                           String solutionInCorrection, Boolean answerGood, LocalDateTime created) {
        this.ticket = ticket;
        this.userCustomerId = userCustomerId;
        this.userExecutorId = userExecutorId;
        this.solutionInReasons = solutionInReasons;
        this.solutionInCorrection = solutionInCorrection;
        this.answerGood = answerGood;
        this.created = created;
    }
}