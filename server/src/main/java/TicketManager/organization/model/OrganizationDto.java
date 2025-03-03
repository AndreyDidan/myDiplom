package TicketManager.organization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {
    private Long organizationId;
    private String organizationName;
    private String littleNameOrganization;
    private String nameDirector;
    private String surnameDirector;
    private Long inn;
    private Integer okved;
    private String adress;
    private String phoneOrganization;
    private String emailOrganization;
}
