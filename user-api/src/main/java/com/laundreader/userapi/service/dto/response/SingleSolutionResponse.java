package com.laundreader.userapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleSolutionResponse {
    private List<String> additionalInfo;
    private List<solution> solutions;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class solution{
        private String name;
        private String contents;
    }
}
