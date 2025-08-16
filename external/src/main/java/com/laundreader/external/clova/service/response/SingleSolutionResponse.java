package com.laundreader.external.clova.service.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleSolutionResponse {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<solution> solutions;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class solution{
        private String name;
        private String contents;
    }
}
