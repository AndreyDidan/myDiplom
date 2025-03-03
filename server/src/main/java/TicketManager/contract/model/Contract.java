package TicketManager.contract.model;

import TicketManager.contract.technicalspecification.model.TechnicalSpecification;
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
@Table(name = "contracts_or_orders")
public class Contract {

    @Id
    @Column(name = "contracts_or_orders_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long contractId;

    @Column(name = "name_contract")
    private String nameContract;

    @Column(name = "number_contract")
    private String numberContract;

    //Тип контракта(Создание функции/Сопровождение программного обеспечения/Сопровождение инфраструктуры)
    @Column(name = "type")
    private String type;

    @Column(name = "date_open_contract")
    private LocalDateTime dateOpenContract;

    @Column(name = "date_close_contract")
    private LocalDateTime dateCloseContract;

    @Column(name = "price_contract")
    private Double priceContract;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technical_specification_id")
    private TechnicalSpecification technicalSpecification;

    @Column(name = "organization_customer")
    private String organizationCustomer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_customer")
    private MyUser userCustomer;

    @Column(name = "organization_perfomer")
    private String organizationPerfomer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_perfomer")
    private MyUser userPerfomer;

    @Column(name = "organization_execution")
    private String organizationExecution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_execution")
    private MyUser userExecution;

    public Contract(String nameContract, String numberContract, String type, LocalDateTime dateOpenContract,
                    LocalDateTime dateCloseContract, Double priceContract, TechnicalSpecification technicalSpecification,
                    String organizationCustomer, MyUser userCustomer, String organizationPerfomer,
                    MyUser userPerfomer, String organizationExecution, MyUser userExecution) {
        this.nameContract = nameContract;
        this.numberContract = numberContract;
        this.type = type;
        this.dateOpenContract = dateOpenContract;
        this.dateCloseContract = dateCloseContract;
        this.priceContract = priceContract;
        this.technicalSpecification = technicalSpecification;
        this.organizationCustomer = organizationCustomer;
        this.userCustomer = userCustomer;
        this.organizationPerfomer = organizationPerfomer;
        this.userPerfomer = userPerfomer;
        this.organizationExecution = organizationExecution;
        this.userExecution = userExecution;
    }

    public Contract(String nameContract, String numberContract, LocalDateTime dateOpenContract,
                    LocalDateTime dateCloseContract, Double priceContract, String organizationCustomer,
                    MyUser userCustomer, String organizationPerfomer,
                    MyUser userPerfomer, String organizationExecution, MyUser userExecution) {
        this.nameContract = nameContract;
        this.numberContract = numberContract;
        this.dateOpenContract = dateOpenContract;
        this.dateCloseContract = dateCloseContract;
        this.priceContract = priceContract;
        this.organizationCustomer = organizationCustomer;
        this.userCustomer = userCustomer;
        this.organizationPerfomer = organizationPerfomer;
        this.userPerfomer = userPerfomer;
        this.organizationExecution = organizationExecution;
        this.userExecution = userExecution;
    }
}