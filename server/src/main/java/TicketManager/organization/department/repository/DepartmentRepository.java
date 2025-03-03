package TicketManager.organization.department.repository;

import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.positions.model.Position;
import TicketManager.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d WHERE d.nameDepartment = :nameDepartment AND d.organizationId.id = :organizationId")
    Optional<Department> findByNameDepartmentAndOrganizationId(String nameDepartment, Long organizationId);
    List<Department> findAll();
    // Добавляем метод для поиска отделов по организации
    List<Department> findByOrganizationId(Organization organization);

    @Query("SELECT d FROM Department d WHERE d.organizationId.id = :organizationId")
    List<Department> findAllDepartmentOrganization(@Param("organizationId") Long organizationId);
}