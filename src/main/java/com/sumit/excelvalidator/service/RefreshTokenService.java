package com.sumit.excelvalidator.service;

import com.sumit.excelvalidator.entity.User;
import com.sumit.excelvalidator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${app.jwt.refresh-token.expiration:604800000}")
    private long refreshTokenExpirationMs;

    /**
     * Create and save a refresh token for a user
     */
    public String createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String refreshToken = jwtService.generateRefreshToken(email);
        long expiryTime = System.currentTimeMillis() + refreshTokenExpirationMs;

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiryDate(new Date(expiryTime));
        userRepository.save(user);

        logger.info("Refresh token created for user: {}", email);
        return refreshToken;
    }

    /**
     * Verify and retrieve a refresh token
     */
    public User verifyRefreshToken(String token) {
        User user = userRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (user.getRefreshTokenExpiryDate().before(new Date())) {
            logger.warn("Refresh token expired for user: {}", user.getEmail());
            throw new RuntimeException("Refresh token expired");
        }

        if (!jwtService.isRefreshTokenValid(token)) {
            logger.warn("Refresh token validation failed for user: {}", user.getEmail());
            throw new RuntimeException("Invalid refresh token");
        }

        logger.info("Refresh token verified for user: {}", user.getEmail());
        return user;
    }

    /**
     * Revoke a refresh token
     */
    public void revokeRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRefreshToken(null);
        user.setRefreshTokenExpiryDate(null);
        userRepository.save(user);

        logger.info("Refresh token revoked for user: {}", email);
    }
}

