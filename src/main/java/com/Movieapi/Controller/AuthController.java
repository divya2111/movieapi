package com.Movieapi.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Movieapi.auth.Model.RefreshToken;
import com.Movieapi.auth.Model.User;
import com.Movieapi.auth.Service.AuthService;
import com.Movieapi.auth.Service.JwtService;
import com.Movieapi.auth.Service.RefreshTokenService;
import com.Movieapi.auth.Utils.AuthResponse;
import com.Movieapi.auth.Utils.LoginRequest;
import com.Movieapi.auth.Utils.RefreshTokenRequest;
import com.Movieapi.auth.Utils.RegisterRequest;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {
	
	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;
	
	
	public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
		super();
		this.authService = authService;
		this.refreshTokenService = refreshTokenService;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
		return ResponseEntity.ok(authService.register(registerRequest));	
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
		
		RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
		User user = refreshToken.getUser();
		
		String accessToken = jwtService.generateToken(user);
		
		return ResponseEntity.ok(AuthResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getRefreshToken())
				.build());
	}
	
	

}
