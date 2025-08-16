package com.laundreader.userapi.service.response.laundry;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SingleSolutionResponseDTO {
    private List<SolutionDTO> solutions;

    @Getter
    @AllArgsConstructor
    public static class SolutionDTO{
        private String name;
        private String contents;
    }
}
