package com.laundreader.userapi._core.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.laundreader.domain.entity.user.User;
import com.laundreader.userapi._core.AppConstants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	@Value("${jwt.key}")
	private String secretKey;
	private Key JWT_KEY;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		JWT_KEY = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(User user) {
		return Jwts.builder()
			.setSubject(user.getEmail())
			.claim("role", String.valueOf(user.getRole()))
			.setExpiration(new Date(System.currentTimeMillis() + AppConstants.ACCESS_TOKEN_EXP))
			.signWith(JWT_KEY, SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken() {
		return Jwts.builder()
			.setExpiration(new Date(System.currentTimeMillis() + AppConstants.REFRESH_TOKEN_EXP))
			.signWith(JWT_KEY, SignatureAlgorithm.HS256)
			.compact();
	}

	// 토큰 검증
	public boolean isTokenValid(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(JWT_KEY)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			throw new JwtException("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			throw new JwtException("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new JwtException("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new JwtException("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new JwtException("JWT claims string is empty.");
		}
	}

	// 토큰에서 email 정보 추출
	public String getEmail(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(JWT_KEY)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	// 토큰에서 유효기간 추출
	public Long getRemainingMs(String jwt) {
		try {
			Date expiration = Jwts.parserBuilder()
				.setSigningKey(JWT_KEY)
				.build()
				.parseClaimsJws(jwt)
				.getBody()
				.getExpiration();

			Long now = System.currentTimeMillis();
			long remainingMs = expiration.getTime() - now;
			return Math.max(0, remainingMs); //음수 방지
		} catch (ExpiredJwtException e) {
			return 0L;
		}
	}
}
