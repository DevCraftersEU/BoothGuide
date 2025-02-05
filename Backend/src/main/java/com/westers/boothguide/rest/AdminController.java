package com.westers.boothguide.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/auth/checkAuth")
    public ResponseEntity<String[]> checkAuth(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().toArray(new String[0]));
    }


}
