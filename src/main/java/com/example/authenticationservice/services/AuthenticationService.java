package com.example.authenticationservice.services;

import com.example.authenticationservice.entities.User;
import com.example.authenticationservice.repositories.UserRepository;
import com.example.authenticationservice.requests.AuthenticationRequest;
import com.example.authenticationservice.requests.AuthenticationResponse;
import com.example.authenticationservice.requests.RegisterRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @NonNull
    private StringRedisTemplate redisTemplate;
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with such username already exists!");
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with such email already exists!");
        }
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

         // Cache the token in Redis with a TTL (e.g., 3600 seconds)
        redisTemplate.opsForValue().set(jwtToken, jwtToken, Duration.ofSeconds(3600)); // Cache with a TTL

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String jwtToken = jwtService.generateToken(user);

        // Cache the token in Redis with a TTL (e.g., 3600 seconds)
        redisTemplate.opsForValue().set(jwtToken, jwtToken, Duration.ofSeconds(3600)); // Cache with a TTL

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }
}