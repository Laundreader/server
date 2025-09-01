package com.laundreader.userapi.dto.image;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageDTO {
	String format;
	String data;
}