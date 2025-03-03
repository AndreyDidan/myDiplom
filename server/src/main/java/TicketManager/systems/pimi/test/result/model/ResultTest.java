package TicketManager.systems.pimi.test.result.model;


import TicketManager.systems.pimi.test.appruvs.model.TestAppruvs;
import TicketManager.systems.pimi.test.model.Tests;
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
@Table(name = "result_tests")
public class ResultTest {
    @Id
    @Column(name = "result_tests_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private Tests testId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tappruvs_customer_id")
    private TestAppruvs appruvsCustomer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appruvs_perfomer_id")
    private TestAppruvs appruvsPerfomer;

    private Boolean result;
}
