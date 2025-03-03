package TicketManager.systems.pimi.model;

import TicketManager.systems.functionssystem.model.FunctionSystem;
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
@Table(name = "pimis")
public class Pimi {

    @Id
    @Column(name = "pimi_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "function_system_id")
    private FunctionSystem functionsSystems;
}
