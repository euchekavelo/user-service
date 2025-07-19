package ru.tw1.euchekavelo.dto.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttributesRequestDto {

    @JsonProperty("user_id")
    private List<String> userId;
}
