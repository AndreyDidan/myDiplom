package TicketManager.ticket.fuctionsposition.repository;

import TicketManager.organization.department.positions.model.Position;
import TicketManager.ticket.fuctionsposition.model.FunctionsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FunctionsPositionRepository extends JpaRepository<FunctionsPosition, Long> {
    List<FunctionsPosition> findAll();
    List<FunctionsPosition> findAllByPositionId(Position positionId);

    /*//Поиск функций по должности и имени функции
    @Query("SELECT fp FROM FunctionsPosition fp " +
            "JOIN fp.functionSystems fs " +
            "WHERE fp.positionId = :position AND fs.nameFunctionSystem = :nameFunctionSystem")
    List<FunctionsPosition> findByPositionAndFunctionName(@Param("position") Position position,
                                                          @Param("nameFunctionSystem") String nameFunctionSystem);*/

    @Query("SELECT fp FROM FunctionsPosition fp " +
            "JOIN fp.functionSystems fs " +
            "WHERE fp.positionId = :position AND fs.nameFunctionSystem = :nameFunctionSystem")
    Optional<FunctionsPosition> findByPositionAndFunctionSystem(@Param("position") Position position,
                                                                @Param("nameFunctionSystem") String nameFunctionSystem);
}
