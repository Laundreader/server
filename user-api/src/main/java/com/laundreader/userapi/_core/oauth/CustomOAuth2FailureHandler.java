package com.laundreader.userapi._core.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
	private static final String REDIRECT_URI = "https://laundreader.com/auth/callback";

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		String errorCode = "500";
		String errorMessage = "알 수 없는 오류가 발생했습니다.";

		if (exception instanceof OAuth2AuthenticationException oAuth2Exception) {
			errorCode = oAuth2Exception.getError().getErrorCode();
			errorMessage = oAuth2Exception.getMessage();
		}

		response.sendRedirect(REDIRECT_URI + "?success=false" +
			"&code=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8) +
			"&message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
	}
}

