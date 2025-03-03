package TicketManager.organization.department.model;

import TicketManager.organization.model.Organization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "department_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(name = "name_department")
    private String nameDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organizationId;


    public Department(String nameDepartment, Organization organizationId) {
        this.nameDepartment = nameDepartment;
        this.organizationId = organizationId;
    }
}