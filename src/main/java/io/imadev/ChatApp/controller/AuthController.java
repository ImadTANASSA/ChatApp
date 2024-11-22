package io.imadev.ChatApp.controller;

import io.imadev.ChatApp.dto.AuthResponse;
import io.imadev.ChatApp.dto.LoginRequest;
import io.imadev.ChatApp.dto.RegisterRequest;
import io.imadev.ChatApp.service.AuthService;
import io.imadev.ChatApp.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Login request: " + request);
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(OAuth2AuthenticationToken authentication) {


        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Ajouter des revendications personnalisées
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);

        // Générer un jeton JWT
        String token = jwtService.generateToken(claims, email);

        // Retourner le jeton et les informations utilisateur
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "name", name
        ));
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<?> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google OAuth2 authentication failed.");
    }

    @GetMapping("/test")
    public String test() {
        return "Server is running";
    }
}
