package TicketManager.contract.technicalspecification.model;

import TicketManager.organization.model.Organization;
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
@Table(name = "technical_specifications")
public class TechnicalSpecification {

    @Id
    @Column(name = "technical_specification_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long specificationId;

    @Column(name = "name_technical_specification")
    private String nameSpecification;

    //заказчик
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_customer_id")
    private Organization customer;

    //исполнитель
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_perfomer_id")
    private Organization perfomer;

    //проверяющий
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_execution_id")
    private Organization execution;

    public TechnicalSpecification(String nameSpecification, Organization customer, Organization perfomer, Organization execution) {
        this.nameSpecification = nameSpecification;
        this.customer = customer;
        this.perfomer = perfomer;
        this.execution = execution;
    }

    public TechnicalSpecification(String nameSpecification, Organization customer, Organization perfomer) {
        this.nameSpecification = nameSpecification;
        this.customer = customer;
        this.perfomer = perfomer;
        this.execution = null;
    }
}