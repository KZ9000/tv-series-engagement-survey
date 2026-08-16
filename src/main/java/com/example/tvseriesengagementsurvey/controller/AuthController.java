package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.dto.auth.LoginRequest;
import com.example.tvseriesengagementsurvey.dto.auth.LoginResponse;
import com.example.tvseriesengagementsurvey.dto.auth.RegisterRequest;
import com.example.tvseriesengagementsurvey.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Registro de usuarios e inicio de sesión")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea un usuario con rol USER. El email debe ser único; la contraseña se almacena "
                    + "haseada con BCrypt. Devuelve 201 Created.")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Iniciar sesión",
            description = "Valida las credenciales y devuelve un JWT Bearer para los endpoints protegidos.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
