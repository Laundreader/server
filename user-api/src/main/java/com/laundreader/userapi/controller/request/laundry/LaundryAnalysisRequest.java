package com.laundreader.userapi.controller.request.laundry;

import com.laundreader.userapi.service.dto.ImageDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LaundryAnalysisRequest {
	@NotNull(message = "값이 null 일 수 없습니다.")
	private Image label;
	private Image clothes;

	public ImageDTO toImageDTO(Image image) {
		return ImageDTO.builder()
			.format(image.format)
			.data(image.data)
			.build();
	}

	@Getter
	public static class Image {
		@NotBlank(message = "빈 문자열 입니다.")
		private String format;
		@NotBlank(message = "빈 문자열 입니다.")
		private String data;
	}
}