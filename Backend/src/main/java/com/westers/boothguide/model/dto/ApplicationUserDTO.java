package com.westers.boothguide.model.dto;

import com.westers.boothguide.model.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserDTO {
    private Long id;
    private String username;
    private String password;
    private Collection<ApplicationUser.Role> roles;

    public static ApplicationUserDTO from(ApplicationUser applicationUser, boolean deletePassword) {
        return new ApplicationUserDTO(applicationUser.getId(), applicationUser.getUsername(), deletePassword ? null : applicationUser.getPassword(), applicationUser.getRoles());
    }

    public static ApplicationUserDTO from(ApplicationUser applicationUser) {
        return from(applicationUser, false);
    }
}
