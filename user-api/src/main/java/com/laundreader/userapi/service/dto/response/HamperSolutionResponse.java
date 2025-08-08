package com.laundreader.userapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HamperSolutionResponse {
    private List<group> groups;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class group{
        private int id;
        private String name;
        private String solution;
        private List<Integer> laundryIds;
    }
}
