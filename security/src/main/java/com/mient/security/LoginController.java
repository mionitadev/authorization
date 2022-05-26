package com.mient.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final CustomerRepository customerRepository;
    private final LoginRepository loginRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<CustomerEntity> byUsername = customerRepository.findByUsername(username);
        try {
            CustomerEntity customerEntity = byUsername.orElseThrow(() -> new BadCredentialsException("Bad Credentials"));

            if (customerEntity.getUsername().equals(username) && customerEntity.getPassword().equals(password)) {
                return ResponseEntity.ok(createTokenResponse(customerEntity));
            }
            return ResponseEntity.status(401).build();
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }

    private TokenResponse createTokenResponse(CustomerEntity customerEntity) {
        TokenResponse token = TokenResponse.createToken();
        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setCustomerId(customerEntity.getId());
        loginEntity.setToken(token.getToken());
        loginEntity.setExpireDateTime(token.expireDateTime);
        loginRepository.save(loginEntity);
        return token;
    }

    @Data
    private static class TokenResponse {
        private String token;
        private LocalDateTime expireDateTime;

        public static TokenResponse createToken() {
            TokenResponse token = new TokenResponse();
            token.setToken(UUID.randomUUID().toString());
            LocalDateTime ttl = LocalDateTime.now().plusHours(1);
            token.setExpireDateTime(ttl);
            return token;
        }
    }
}
