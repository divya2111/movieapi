package com.Movieapi.auth.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.Movieapi.auth.Model.User;

import jakarta.transaction.Transactional;

public interface UserRepo extends JpaRepository<User,Integer> {

	Optional<User> findByEmail(String username);
	
	@Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

}
