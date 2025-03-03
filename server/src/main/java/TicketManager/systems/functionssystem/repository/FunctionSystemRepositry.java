package TicketManager.systems.functionssystem.repository;

import TicketManager.contract.model.Contract;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.model.System;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FunctionSystemRepositry extends JpaRepository<FunctionSystem, Long> {
    Optional<FunctionSystem> findByNameFunctionSystem(String nameFunctionSystem);
    List<FunctionSystem> findAll();
    List<FunctionSystem> findBySystemId(System systemId);

    @Query("SELECT fs FROM FunctionSystem fs WHERE fs.systemId.id = :systemId")
    List<FunctionSystem> findAllFunctionSystems(@Param("systemId") Long systemId);
    List<FunctionSystem> findByNameFunctionSystemIn(List<String> nameFunctionSystems);
    List<FunctionSystem> findBySystemIdAndContractId(System system, Contract contract);

    @Query("SELECT DISTINCT fs.systemId FROM FunctionSystem fs WHERE fs.functionId IN :functionIds")
    List<System> findUniqueSystemsByFunctionIds(@Param("functionIds") List<Long> functionIds);
}