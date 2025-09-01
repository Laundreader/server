package com.laundreader.userapi.dto.laundry;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SolutionDTO {
    String name;
    String contents;
}
