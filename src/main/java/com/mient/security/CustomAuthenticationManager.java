package com.mient.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final CustomerRepository customerRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        CustomerEntity entity = customerRepository.findByUsername(String.valueOf(authentication.getPrincipal())).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (entity.getUsername().equals(authentication.getPrincipal()) && entity.getPassword().equals(authentication.getCredentials())) {
            authorities.add(new SimpleGrantedAuthority(entity.getRole()));
            return UsernamePasswordAuthenticationToken.authenticated(authentication.getPrincipal(), authentication.getCredentials(), authorities);
        }

        throw new BadCredentialsException("Invalid credentials");
    }
}
