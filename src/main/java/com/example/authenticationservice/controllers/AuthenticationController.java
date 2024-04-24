package com.example.authenticationservice.controllers;

import com.example.authenticationservice.requests.AuthenticationRequest;
import com.example.authenticationservice.requests.RegisterRequest;
import com.example.authenticationservice.requests.AuthenticationResponse;
import com.example.authenticationservice.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication-service")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    AuthenticationResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}