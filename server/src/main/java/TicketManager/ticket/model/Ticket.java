package TicketManager.ticket.model;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.model.Contract;
import TicketManager.ticket.problem.model.Problem;
import TicketManager.user.model.MyUser;
import lombok.Builder;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(name = "ticket_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_creater_id")
    private MyUser author;
    @Column(name = "date_created")
    private LocalDateTime dateCreate;
    @Column(name = "little_description")
    private String littleDescription;
    @Column(name = "description")
    private String description;
    @Column(name = "function")
    private String nameFunction;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problems_id")
    private Problem problemFunctionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contractId;
    @Column(name = "organization_perfomer_id")
    private String organizationPerfomerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "limits_id")
    private Limit limitsId;
    @Column(name = "date_closed")
    private LocalDateTime dateClose;
    @Column(name = "expired")
    private Boolean isExpired;
    @Enumerated(EnumType.STRING)
    @Column(name = "name_status")
    private Status status;

    public Ticket(MyUser author, LocalDateTime dateCreate, String littleDescription, String description, String nameFunction,
                  Problem problemFunctionId, Contract contractId, String organizationPerfomerId,
                  Limit limitsId, Boolean isExpired, Status status) {
        this.author = author;
        this.dateCreate = dateCreate;
        this.littleDescription = littleDescription;
        this.nameFunction = nameFunction;
        this.description = description;
        this.problemFunctionId = problemFunctionId;
        this.contractId = contractId;
        this.organizationPerfomerId = organizationPerfomerId;
        this.limitsId = limitsId;
        this.isExpired = isExpired;
        this.status = status;
    }

    public Ticket(MyUser author, LocalDateTime dateCreate, String littleDescription, String description, String nameFunction,
                  Contract contractId, String organizationPerfomerId, Limit limitsId, Boolean isExpired, Status status) {
        this.author = author;
        this.dateCreate = dateCreate;
        this.littleDescription = littleDescription;
        this.nameFunction = nameFunction;
        this.description = description;
        this.contractId = contractId;
        this.organizationPerfomerId = organizationPerfomerId;
        this.limitsId = limitsId;
        this.isExpired = isExpired;
        this.status = status;
    }

    public Ticket(String description, MyUser author) {
        this.description = description;
        this.author = author;
    }

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }
}