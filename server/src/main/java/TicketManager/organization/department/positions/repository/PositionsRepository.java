package TicketManager.organization.department.positions.repository;

import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.positions.model.Position;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionsRepository extends JpaRepository<Position, Long> {
    Optional<Position> findById(Long id);
    Optional<Position> findByNamePosition(String namePosition);
    List<Position> findAll();
    List<Position> findByDepartmentId(Department department);

    @Query("SELECT p FROM Position p WHERE p.namePosition = :namePosition AND p.departmentId.id = :departmentId")
    Optional<Position> findByNamePositionAndDepartmentId(@Param("namePosition") String namePosition,
                                                         @Param("departmentId") Long departmentId);


    @Query("SELECT d FROM Position d WHERE d.departmentId.id = :departmentId")
    List<Position> findAllPositionsDepartment(@Param("departmentId") Long departmentId);

    @Modifying
    @Query("INSERT INTO Position (namePosition, descriptionPosition, departmentId) VALUES (:namePosition, :descriptionPosition, :departmentId)")
    void savePosition(@Param("namePosition") String namePosition,
                      @Param("descriptionPosition") String descriptionPosition,
                      @Param("departmentId") Long departmentId);

    /*@Query("SELECT p FROM Position p WHERE p.namePosition = :namePosition AND p.organizationId.id = :organizationId AND p.departmentId.id = :departmentId")
    Optional<Position> findByNamePositionAndOrganizationAndDepartment(String namePosition, Long organizationId, Long departmentId);*/
}