package TicketManager.systems.pimi.test.model;

import TicketManager.systems.pimi.model.Pimi;
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
@Table(name = "tests")
public class Tests {

    @Id
    @Column(name = "test_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pimi_id")
    private Pimi pimiId;

    @Column(name = "name_test")
    private String nameTest;

    @Column(name = "date_test")
    private LocalDateTime dateTest;

    @Column(name = "description")
    private String descriptionTest;

    @Column(name = "data_input")
    private String dataInput;

    @Column(name = "expected_output")
    private String expectedOutput;

    @Column(name = "actual_output")
    private String actualOutput;
}
