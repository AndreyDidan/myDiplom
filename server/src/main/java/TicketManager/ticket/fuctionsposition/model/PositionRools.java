package TicketManager.ticket.fuctionsposition.model;

import TicketManager.organization.department.positions.model.Position;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "position_rools")
public class PositionRools {
    @Id
    @Column(name = "position_rools_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roolsId;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position positionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rools")
    private Rools rools;
}