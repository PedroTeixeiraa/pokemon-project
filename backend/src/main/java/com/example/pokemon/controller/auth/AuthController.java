package com.example.pokemon.controller.auth;

import com.example.pokemon.service.auth.AuthUser;
import com.example.pokemon.service.auth.LoginCommand;
import com.example.pokemon.service.auth.LoginResult;
import com.example.pokemon.service.auth.LoginService;
import com.example.pokemon.service.auth.RegisterUserCommand;
import com.example.pokemon.service.auth.RegisterUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserService registerUserUseCase;
    private final LoginService loginUseCase;

    public AuthController(RegisterUserService registerUserUseCase, LoginService loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthUser> register(@Valid @RequestBody AuthDtos.Credentials request) {
        AuthUser user = registerUserUseCase.execute(new RegisterUserCommand(request.email(), request.password()));
        return ResponseEntity.created(URI.create("/api/users/" + user.id())).body(user);
    }

    @PostMapping("/login")
    public LoginResult login(@Valid @RequestBody AuthDtos.Credentials request) {
        return loginUseCase.execute(new LoginCommand(request.email(), request.password()));
    }
}
