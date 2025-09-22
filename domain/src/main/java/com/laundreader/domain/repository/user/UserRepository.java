package com.laundreader.domain.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.user.User;
import com.laundreader.domain.type.user.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);
}
