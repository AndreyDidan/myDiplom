package TicketManager.systems.functionssystem.model;

import TicketManager.contract.model.Contract;
import TicketManager.systems.model.System;
import TicketManager.systems.virtualmachines.model.VirtualMachine;
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
@Table(name = "functions_systems")
public class FunctionSystem {
    @Id
    @Column(name = "function_system_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long functionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "name_system")
    private System systemId;

    @Column(name = "description_function")
    private String description;

    @Column(name = "name_function_system")
    private String nameFunctionSystem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private Contract contractId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "virtual_machin_id")
    private VirtualMachine ipVirtualMachine;

    @Column(name = "port")
    private String port;

    @Column(name = "function_accept")
    private Boolean functionAccept;

    public FunctionSystem(System systemId, String nameFunctionSystem, String description, Contract contractId,
                          VirtualMachine ipVirtualMachine, String port, Boolean functionAccept) {
        this.systemId = systemId;
        this.nameFunctionSystem = nameFunctionSystem;
        this.description = description;
        this.contractId = contractId;
        this.ipVirtualMachine = ipVirtualMachine;
        this.port = port;
        this.functionAccept = functionAccept;
    }

    public FunctionSystem(System systemId, String nameFunctionSystem, String description, Contract contractId,
                          VirtualMachine ipVirtualMachine, String port) {
        this.systemId = systemId;
        this.nameFunctionSystem = nameFunctionSystem;
        this.description = description;
        this.contractId = contractId;
        this.ipVirtualMachine = ipVirtualMachine;
        this.port = port;
    }
}