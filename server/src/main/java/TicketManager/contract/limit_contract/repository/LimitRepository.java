package TicketManager.contract.limit_contract.repository;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.model.Contract;
import TicketManager.organization.department.model.Department;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.model.System;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findAll();
    List<Limit> findByLevelName(String levelName);
    @Query("SELECT l FROM Limit l WHERE l.levelName = :levelName AND l.contractId.numberContract = :numberContract")
    List<Limit> findByLevelNameAndContractNumber(@Param("levelName") String levelName,
                                                 @Param("numberContract") String numberContract);

    @Query("SELECT l FROM Limit l WHERE l.levelName = :levelName " +
            "AND l.contractId.numberContract = :numberContract " +
            "AND l.penaltysStart = :penaltysStart")
    List<Limit> findByLevelNameAndContractNumberAndPenaltyStart(
            @Param("levelName") String levelName,
            @Param("numberContract") String numberContract,
            @Param("penaltysStart") Duration penaltysStart);

    @Query("SELECT d FROM Limit d WHERE d.contractId.id = :contractId")
    List<Limit> findAllLimitContract(@Param("contractId") Long contractId);

    @Query("SELECT d FROM Limit d WHERE d.levelName = :levelName AND d.contractId.id = :contractId")
    Optional<Limit> findByLevelNameAndContractId(@Param("levelName") String levelName, @Param("contractId") Long contractId);


    List<Limit> findByContractId(Contract contractId);
}
