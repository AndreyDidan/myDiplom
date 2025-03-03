package TicketManager.organization.department.positions.model;

import TicketManager.organization.department.model.Department;
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
@Table(name = "positions")
public class Position {

    @Id
    @Column(name = "position_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @Column(name = "name_position")
    private String namePosition;

    @Column(name = "description_position")
    private String descriptionPosition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department departmentId;

    public Position(String namePosition, String descriptionPosition, Department departmentId) {
        this.namePosition = namePosition;
        this.descriptionPosition = descriptionPosition;
        this.departmentId = departmentId;
    }
}
