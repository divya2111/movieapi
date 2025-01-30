package com.Movieapi.auth.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Movieapi.auth.Model.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Integer>{
	Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
