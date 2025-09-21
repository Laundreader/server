package com.laundreader.userapi.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMeResponse {
	private String email;
	private String provider;
	private String nickname;
}
