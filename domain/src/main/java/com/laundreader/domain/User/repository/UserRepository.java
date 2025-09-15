package com.laundreader.domain.User.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.User.entity.User;
import com.laundreader.domain.User.type.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);
}
