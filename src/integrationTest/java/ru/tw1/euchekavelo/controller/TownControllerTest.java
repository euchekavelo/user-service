package ru.tw1.euchekavelo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tw1.euchekavelo.config.PostgreSQLContainerConfig;
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.TOWN_NOT_FOUND_EXCEPTION_MESSAGE;

@SpringBootTest(classes = PostgreSQLContainerConfig.class)
@AutoConfigureMockMvc
class TownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTown() throws Exception {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("test");
        mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void getTownByIdSuccess() throws Exception {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townRequestDto);

        mockMvc.perform(get("/towns/" + townId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andDo(print());
    }

    @Test
    void getTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");

        mockMvc.perform(get("/towns/" + townId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void deleteTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");

        mockMvc.perform(delete("/towns/" + townId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void deleteTownByIdSuccess() throws Exception {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townRequestDto);

        mockMvc.perform(delete("/towns/" + townId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateTownByIdSuccess() throws Exception {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townRequestDto);

        TownRequestDto newTownRequestDto = new TownRequestDto();
        newTownRequestDto.setName("Moscow");

        mockMvc.perform(put("/towns/" + townId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTownRequestDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        TownRequestDto newTownRequestDto = new TownRequestDto();
        newTownRequestDto.setName("Moscow");

        mockMvc.perform(put("/towns/" + townId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTownRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andExpect(jsonPath("result").value(false))
                .andDo(print());
    }

    private UUID getNewTownIdFromDatabase(TownRequestDto townRequestDto) throws Exception {
        MvcResult mvcResultTown = mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultTown.getResponse().getContentAsString(), ResponseDto.class).getId();
    }
}