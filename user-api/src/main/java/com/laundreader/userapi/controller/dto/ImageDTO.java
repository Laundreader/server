package com.laundreader.userapi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageDTO {
    String format;
    String data;
}