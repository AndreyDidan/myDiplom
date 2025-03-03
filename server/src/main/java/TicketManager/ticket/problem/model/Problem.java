package TicketManager.ticket.problem.model;

import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.ticket.fuctionsposition.model.FunctionsPosition;
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
@Table(name = "problems_function")
public class Problem {

    @Id
    @Column(name = "problems_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private MyUser userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "function_position_rools_id")
    private FunctionsPosition functionsPosition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "function_id")
    private FunctionSystem functionSystem;

    @Column(name = "date_created_problem")
    private LocalDateTime dateCreatedProblem;

    @Column(name = "litle_description_problem")
    private String litleDescriptionProblem;

    @Column(name = "description_problem")
    private String descriptionProblem;

    @Column(name = "is_recommendation")
    private Boolean isRecommendation;

    @Column(name = "is_contract")
    private Boolean isContract;

    public Problem(MyUser userId, FunctionsPosition functionsPosition, FunctionSystem functionSystem,
                   LocalDateTime dateCreatedProblem, String litleDescriptionProblem, String descriptionProblem,
                   Boolean isRecommendation, Boolean isContract) {
        this.userId = userId;
        this.functionsPosition = functionsPosition;
        this.functionSystem = functionSystem;
        this.dateCreatedProblem = dateCreatedProblem;
        this.litleDescriptionProblem = litleDescriptionProblem;
        this.descriptionProblem = descriptionProblem;
        this.isRecommendation = isRecommendation;
        this.isContract = isContract;
    }

    public Problem(MyUser userId, LocalDateTime dateCreatedProblem, FunctionSystem functionSystem,
                   String litleDescriptionProblem, String descriptionProblem, Boolean isRecommendation,
                   Boolean isContract) {
        this.userId = userId;
        this.dateCreatedProblem = dateCreatedProblem;
        this.functionSystem = functionSystem;
        this.litleDescriptionProblem = litleDescriptionProblem;
        this.descriptionProblem = descriptionProblem;
        this.isRecommendation = isRecommendation;
        this.isContract = isContract;
    }
}