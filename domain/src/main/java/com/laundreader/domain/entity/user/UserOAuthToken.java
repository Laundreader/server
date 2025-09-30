package com.laundreader.domain.entity.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;

import com.laundreader.domain.type.user.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_oauth_token_tb")
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOAuthToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(length = 500)
	private String accessToken;

	@Column(length = 500)
	private String refreshToken;

	private LocalDateTime accessTokenExpireAt;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	public UserOAuthToken(User user) {
		this.user = user;
		this.provider = user.getProvider();
	}
}
