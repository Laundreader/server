package com.laundreader.userapi.service.response.laundry;

import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.List;

@Getter
@AllArgsConstructor
public class HamperSolutionResponseDTO {
    private List<groupDTO> groups;

    @Getter
    @AllArgsConstructor
    public static class groupDTO {
        private int id;
        private String name;
        private String solution;
        private List<Integer> laundryIds;
    }
}
