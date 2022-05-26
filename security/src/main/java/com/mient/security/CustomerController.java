package com.mient.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/customers")
    public ResponseEntity<?> create(@RequestBody CustomerRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setName(request.name);
        entity.setUsername(request.username);
        entity.setPassword(request.password);
        entity.setRole("USER");
        return ResponseEntity.ok(customerRepository.save(entity));
    }

    @GetMapping("/customers")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @Data
    public static class CustomerRequest {
        private String name;
        private String username;
        private String password;
    }
}
