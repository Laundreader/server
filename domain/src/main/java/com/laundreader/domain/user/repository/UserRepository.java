package com.laundreader.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.user.entity.User;
import com.laundreader.domain.user.type.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);
}
