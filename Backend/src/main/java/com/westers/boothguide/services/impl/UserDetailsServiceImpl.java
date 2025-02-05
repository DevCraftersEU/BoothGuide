package com.westers.boothguide.services.impl;

import com.westers.boothguide.model.jpa.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var appUser = applicationUserRepository.getApplicationUserByUsername(username);
        if(appUser.isEmpty())
            throw new UsernameNotFoundException(username);
        return User.withUsername(appUser.get().getUsername())
                .roles(appUser.get().getRoles().stream().map(Enum::toString).toArray(String[]::new))
                .password(appUser.get().getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
