package TicketManager.ticket.fuctionsposition.model;

import TicketManager.organization.department.positions.model.Position;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "functions_positions")
public class FunctionsPosition {
    @Id
    @Column(name = "function_position_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long functionPosityonId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position positionId;

    // Модификация: список функций, а не одна функция
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "function_position_function_system",
            joinColumns = @JoinColumn(name = "function_position_id"),
            inverseJoinColumns = @JoinColumn(name = "function_system_id")
    )
    private List<FunctionSystem> functionSystems;

    @Column(name = "number_contract")
    private String numberContract;

    @Enumerated(EnumType.STRING)
    @Column(name = "rools")
    private Rools rools;

    public FunctionsPosition(Position positionId, String numberContract, List<FunctionSystem> functionSystems, Rools rools) {
        this.positionId = positionId;
        this.numberContract = numberContract;
        this.functionSystems = functionSystems;
        this.rools = rools;
    }
}