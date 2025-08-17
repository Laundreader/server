package com.laundreader.userapi.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageDTO {
	String format;
	String data;
}