package TicketManager.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organizations")
public class Organization {

    @Id
    @Column(name = "organization_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;
    @Column(name = "name_organization")
    private String organizationName;
    @Column(name = "little_name_position")
    private String littleNameOrganization;
    @Column(name = "name_director")
    private String nameDirector;
    @Column(name = "surname_director")
    private String surnameDirector;
    @Column(name = "inn")
    private Long inn;
    @Column(name = "okved")
    private Integer okved;
    @Column(name = "adress")
    private String adress;
    @Column(name = "phone_organization")
    private String phoneOrganization;
    @Email
    @Column(name = "email_organization")
    private String emailOrganization;

    public Organization(String organizationName, String littleNameOrganization, String nameDirector,
                        String surnameDirector, Long inn, Integer okved, String adress, String phoneOrganization,
                        String emailOrganization) {
        this.organizationName = organizationName;
        this.littleNameOrganization = littleNameOrganization;
        this.nameDirector = nameDirector;
        this.surnameDirector = surnameDirector;
        this.inn = inn;
        this.okved = okved;
        this.adress = adress;
        this.phoneOrganization = phoneOrganization;
        this.emailOrganization = emailOrganization;
    }
}