package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.auth.LoginRequest;
import com.example.tvseriesengagementsurvey.dto.auth.LoginResponse;
import com.example.tvseriesengagementsurvey.dto.auth.RegisterRequest;
import com.example.tvseriesengagementsurvey.entity.Role;
import com.example.tvseriesengagementsurvey.entity.User;
import com.example.tvseriesengagementsurvey.exception.EmailAlreadyExistsException;
import com.example.tvseriesengagementsurvey.repository.UserRepository;
import com.example.tvseriesengagementsurvey.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        return new LoginResponse(jwtService.generateToken(userDetails), "Bearer");
    }
}
