package TicketManager.contract.repository;


import TicketManager.contract.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByNameContract(String nameContract);
    List<Contract> findAll();
    Optional<Contract> findByNumberContract(String numberContract);
}
