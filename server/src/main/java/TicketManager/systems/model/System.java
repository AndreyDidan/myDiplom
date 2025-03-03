package TicketManager.systems.model;

import TicketManager.contract.model.Contract;
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
@Table(name = "systems")
public class System {
    @Id
    @Column(name = "system_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long systemId;

    @Column(name = "name_system")
    private String nameSystem;

    @Column(name = "little_name_system")
    private String littleNameSystem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private Contract contractId;

    public System(String nameSystem, String littleNameSystem, Contract contractId) {
        this.nameSystem = nameSystem;
        this.littleNameSystem = littleNameSystem;
        this.contractId = contractId;
    }
}