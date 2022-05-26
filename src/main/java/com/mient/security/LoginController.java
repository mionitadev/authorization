package com.mient.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/precess_login")
    public void login() {
        System.out.println("login");
    }
}
