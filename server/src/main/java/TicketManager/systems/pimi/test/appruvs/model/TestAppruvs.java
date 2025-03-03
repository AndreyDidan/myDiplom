package TicketManager.systems.pimi.test.appruvs.model;

import TicketManager.systems.pimi.test.model.Tests;
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
@Table(name = "tests_appruvs")
public class TestAppruvs {
    @Id
    @Column(name = "test_appruvs_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date_appruv")
    private LocalDateTime dateAppruv;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private Tests testId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private MyUser userId;

    @Column(name = "result_user")
    private Boolean resultUser;

    @Column(name = "user_comment")
    private String userComment;
}
