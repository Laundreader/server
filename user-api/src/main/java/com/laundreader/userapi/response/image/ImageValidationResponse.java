package com.laundreader.userapi.response.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageValidationResponse {
	private final Image image;

	public ImageValidationResponse(boolean isValid) {
		this.image = new Image(isValid);
	}

	@Getter
	@AllArgsConstructor
	public static class Image {
		private final boolean isValid;
	}
}
