package com.Movieapi.auth.Service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Movieapi.auth.Model.RefreshToken;
import com.Movieapi.auth.Model.User;
import com.Movieapi.auth.Repo.RefreshTokenRepo;
import com.Movieapi.auth.Repo.UserRepo;

@Service
public class RefreshTokenService {
	
	 private final UserRepo userRepo;

	    private final RefreshTokenRepo refreshTokenRepo;

	    public RefreshTokenService(UserRepo userRepo, RefreshTokenRepo refreshTokenRepo) {
	        this.userRepo = userRepo;
	        this.refreshTokenRepo = refreshTokenRepo;
	    }

	    public RefreshToken createRefreshToken(String username) {
	        User user = userRepo.findByEmail(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));

	        RefreshToken refreshToken = user.getRefreshToken();

	        if (refreshToken == null) {
	            long refreshTokenValidity = 30 * 100000;
	            refreshToken = RefreshToken.builder()
	                    .refreshToken(UUID.randomUUID().toString())
	                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
	                    .user(user)
	                    .build();

	            refreshTokenRepo.save(refreshToken);
	        }

	        return refreshToken;
	    }

	    public RefreshToken verifyRefreshToken(String refreshToken) {
	        RefreshToken refToken = refreshTokenRepo.findByRefreshToken(refreshToken)
	                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

	        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
	            refreshTokenRepo.delete(refToken);
	            throw new RuntimeException("Refresh Token expired");
	        }

	        return refToken;
	    }

}
