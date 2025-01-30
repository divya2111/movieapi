package com.Movieapi.auth.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Movieapi.auth.Model.ForgotPassword;
import com.Movieapi.auth.Model.User;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword,Integer> {
	
	@Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

}
