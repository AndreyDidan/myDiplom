package TicketManager.ticket.recomendations.tickets.model;

import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.ticket.SolutionInClose.model.SolutionInClose;
import TicketManager.ticket.problem.model.Problem;
import TicketManager.user.model.MyUser;
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
public class CorrectionRecomendationTicket {
    @Id
    @Column(name = "correction_recomendations_tickets_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "function_system_id")
    private FunctionSystem functionsSystemsId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problems_id")
    private Problem problemFunctionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solutions_in_close_id")
    private SolutionInClose solutionInCloseId;

    @Column(name = "recommendation_helped")
    private Boolean isRecommendationHelped;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_creater_id")
    private MyUser userCreaterId;

    public CorrectionRecomendationTicket(FunctionSystem functionsSystemsId, Problem problemFunctionId,
                                         Boolean isRecommendationHelped, SolutionInClose solutionInCloseId, MyUser userCreaterId) {
        this.functionsSystemsId = functionsSystemsId;
        this.problemFunctionId = problemFunctionId;
        this.isRecommendationHelped = isRecommendationHelped;
        this.solutionInCloseId = solutionInCloseId;
        this.userCreaterId = userCreaterId;
    }
}