package com.laundreader.userapi.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NicknameChangeRequest {
	@NotBlank(message = "닉네임을 입력해주세요")
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요")
	@Pattern(regexp = "^[가-힣A-Za-z0-9]{2,10}$",
		message = "한글·영문·숫자만 사용 가능하며 공백 및 특수문자는 불가합니다")
	String nickname;
}
