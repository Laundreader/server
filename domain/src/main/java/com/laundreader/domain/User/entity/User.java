package com.laundreader.domain.User.entity;

import java.security.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.laundreader.domain.User.type.Provider;
import com.laundreader.domain.User.type.Role;
import com.laundreader.domain.User.type.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tb")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255, unique = true)
	private String email;

	@Column(nullable = false, length = 60)
	private String password;

	@Column(nullable = false, length = 10)
	private Provider provider;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'USER'")
	private Role role;

	@Column(nullable = false, length = 30)
	private String nickName;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'ACTIVE'")
	private UserStatus status;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	public void withdraw() {
		this.status = UserStatus.WITHDRAW;
	}

	public void active() {
		this.status = UserStatus.ACTIVE;
	}

	public void block() {
		this.status = UserStatus.BLOCK;
	}
}

