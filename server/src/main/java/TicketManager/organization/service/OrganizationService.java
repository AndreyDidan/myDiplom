package TicketManager.organization.service;

import TicketManager.organization.model.Organization;
import TicketManager.organization.model.OrganizationDto;
import java.util.List;

public interface OrganizationService {
    List<OrganizationDto> getAllOrganization();
    Organization getForLittleNameOrganization(String littleNameOrganization);
    Organization getOrganizationForId(Long organizationId);
    Organization addOrganization(String organizationName, String littleNameOrganization, String nameDirector,
                                    String surnameDirector, Long inn, Integer okved, String adress, String phoneOrganization,
                                    String emailOrganization);
    Organization updateOrganization(Organization updatedOrganization);
    void deleteOrganizationForLittleName(String littleNameOrganization);
    void deleteOrganizationForId(Long organizationId);
}
