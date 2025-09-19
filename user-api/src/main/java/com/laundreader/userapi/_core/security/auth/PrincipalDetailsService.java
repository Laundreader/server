package com.laundreader.userapi._core.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.laundreader.common.error.exception.Exception404;
import com.laundreader.domain.user.entity.User;
import com.laundreader.domain.user.repository.UserRepository;
import com.laundreader.domain.user.type.UserStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
			.orElseThrow(() -> new Exception404("아이디(이메일)가 존재하지 않습니다."));

		return new PrincipalDetails(user);
	}
}
