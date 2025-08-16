package com.laundreader.external.clova.service.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HamperSolutionResponse {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<group> groups;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class group{
        private int id;
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private String name;
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private String solution;
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Integer> laundryIds;
    }
}
