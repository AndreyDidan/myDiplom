package TicketManager.user.role;

import TicketManager.user.model.MyUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MyUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}