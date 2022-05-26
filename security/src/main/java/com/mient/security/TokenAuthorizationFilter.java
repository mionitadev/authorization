package com.mient.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";

    private final LoginRepository loginRepository;
    private final CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HEADER);
        try {
            setUpSpringAuthentication(isAuthorized(token));
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private Integer isAuthorized(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("Token expired");
        }
        Optional<LoginEntity> byToken = loginRepository.findByToken(token);
        LoginEntity loginEntity = byToken.orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        if (loginEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token expired");
        }
        return loginEntity.getCustomerId();
    }

    private void setUpSpringAuthentication(Integer customerId) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        authorities.add(new SimpleGrantedAuthority(customerEntity.getRole()));
        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(customerEntity.getUsername(), customerEntity.getPassword(), authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticated);

    }
}
