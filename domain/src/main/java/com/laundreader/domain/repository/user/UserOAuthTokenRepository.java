package com.laundreader.domain.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.user.UserOAuthToken;

public interface UserOAuthTokenRepository extends JpaRepository<UserOAuthToken, Long> {
	Optional<UserOAuthToken> findByUserId(Long id);
}
