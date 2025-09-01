package com.laundreader.userapi.request.image;

import com.laundreader.userapi.dto.image.ImageDTO;
import com.laundreader.userapi.type.ImageType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ImageValidationRequest {
	@NotNull(message = "값이 null 일 수 없습니다.")
	private ImageType type;
	@NotNull(message = "값이 null 일 수 없습니다.")
	private Image image;

	public ImageDTO toImageDTO() {
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
