package com.westers.boothguide.model;

import com.westers.boothguide.model.dto.ApplicationUserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    private Collection<Role> roles;

    public enum Role {
        DESIGNER, MODERATOR, ADMIN
    }

    public static ApplicationUser fromDTO(ApplicationUserDTO dto) {
        return new ApplicationUser(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getRoles());
    }
}
