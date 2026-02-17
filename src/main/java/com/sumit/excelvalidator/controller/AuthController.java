package com.sumit.excelvalidator.controller;

import com.sumit.excelvalidator.entity.User;
import com.sumit.excelvalidator.repository.UserRepository;
import com.sumit.excelvalidator.service.JwtService;
import com.sumit.excelvalidator.dto.AuthResponse;
import com.sumit.excelvalidator.dto.LoginRequest;
import com.sumit.excelvalidator.dto.MessageResponse;
import com.sumit.excelvalidator.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("User registration attempt for email: {}", request.getEmail());

        if (repo.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email already registered - {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Email already registered"));
        }


        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        repo.save(user);
        logger.info("User successfully registered: {}", request.getEmail());
        return ResponseEntity.ok(new MessageResponse("User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("User login attempt for email: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getEmail());
        logger.info("User successfully logged in: {}", request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
