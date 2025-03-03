package TicketManager.systems.virtualmachines.repository;

import TicketManager.systems.virtualmachines.model.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {
    Optional<VirtualMachine> findByNameVM(String nameVM);
    Optional<VirtualMachine> findByNameDNS(String nameDNS);
    Optional<VirtualMachine> findByMachineIp(String machineIp);
    List<VirtualMachine> findAll();
}
