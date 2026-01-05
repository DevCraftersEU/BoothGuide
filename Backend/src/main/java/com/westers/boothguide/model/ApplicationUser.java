package com.westers.boothguide.model;

import com.westers.boothguide.model.dto.ApplicationUserDTO;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_user_seq")
    @SequenceGenerator(name = "application_user_seq", sequenceName = "application_user_seq", allocationSize = 1)
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
