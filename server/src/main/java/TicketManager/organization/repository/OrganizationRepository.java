package TicketManager.organization.repository;

import TicketManager.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByOrganizationName(String organizationName);
    Optional<Organization> findByLittleNameOrganization(String organizationName);
    List<Organization> findAll();
}