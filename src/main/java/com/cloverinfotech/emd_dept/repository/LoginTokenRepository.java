package com.cloverinfotech.emd_dept.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloverinfotech.emd_dept.modal.LoginToken;
import com.cloverinfotech.emd_dept.modal.User;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
	
	Optional<LoginToken> findByToken(String token);
//	@Query("SELECT u FROM LoginToken u WHERE u.username = :username")
//	User findByUserName(@Param("username") String username);
}
