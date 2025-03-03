package TicketManager.organization;

import TicketManager.organization.model.Organization;
import TicketManager.organization.model.OrganizationDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrganizationMapper {
    public static OrganizationDto mapToOrganization(Organization organization) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setOrganizationId(organization.getOrganizationId());
        organizationDto.setOrganizationName(organization.getOrganizationName());
        organizationDto.setLittleNameOrganization(organization.getLittleNameOrganization());
        organizationDto.setNameDirector(organization.getNameDirector());
        organizationDto.setSurnameDirector(organization.getSurnameDirector());
        organizationDto.setInn(organization.getInn());
        organizationDto.setOkved(organization.getOkved());
        organizationDto.setAdress(organization.getAdress());
        organizationDto.setPhoneOrganization(organization.getPhoneOrganization());
        organizationDto.setEmailOrganization(organization.getEmailOrganization());
        return organizationDto;
    }
}
