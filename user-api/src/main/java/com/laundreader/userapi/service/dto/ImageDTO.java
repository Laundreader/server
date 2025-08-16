package com.laundreader.userapi.service.dto;

import com.laundreader.userapi.service.type.ImageType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageDTO {
    ImageType type;
    String format;
    String data;
}