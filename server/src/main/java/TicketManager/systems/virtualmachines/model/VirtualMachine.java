package TicketManager.systems.virtualmachines.model;

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
@Table(name = "virtual_machins")
public class VirtualMachine {
    @Id
    @Column(name = "virtual_machin_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name_virtual_machin")
    private String nameVM;

    @Column(name = "dns_name")
    private String nameDNS;

    @Column(name = "ip_machine")
    private String machineIp;

    public VirtualMachine(String nameVM, String nameDNS, String machineIp) {
        this.nameVM = nameVM;
        this.nameDNS = nameDNS;
        this.machineIp = machineIp;
    }
}