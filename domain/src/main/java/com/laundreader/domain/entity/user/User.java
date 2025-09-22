package com.laundreader.domain.entity.user;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.laundreader.domain.type.user.Provider;
import com.laundreader.domain.type.user.Role;
import com.laundreader.domain.type.user.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tb")
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 10)
	private String ageRange; // 필수

	@Column(nullable = false, length = 30)
	private String nickname; // 선택, 미제공시 임의 지정

	@Column(length = 10)
	private String gender;   // 선택

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'USER'")
	private Role role;

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

