package ru.skillbox.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skillbox.userservice.config.PostgreSQLContainerConfig;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PostgreSQLContainerConfig.class)
@AutoConfigureMockMvc
class TownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTown() throws Exception {
        TownDto townDto = new TownDto();
        townDto.setName("test");
        mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void getTownByIdSuccess() throws Exception {
        TownDto townDto = new TownDto();
        townDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townDto);

        mockMvc.perform(get("/towns/" + townId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andDo(print());
    }

    @Test
    void getTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");

        mockMvc.perform(get("/towns/" + townId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The town with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void deleteTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");

        mockMvc.perform(delete("/towns/" + townId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The town with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void deleteTownByIdSuccess() throws Exception {
        TownDto townDto = new TownDto();
        townDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townDto);

        mockMvc.perform(delete("/towns/" + townId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message").value("The town was successfully removed."))
                .andDo(print());
    }

    @Test
    void updateTownByIdSuccess() throws Exception {
        TownDto townDto = new TownDto();
        townDto.setName("Test town");
        UUID townId = getNewTownIdFromDatabase(townDto);

        TownDto newTownDto = new TownDto();
        newTownDto.setName("Moscow");

        mockMvc.perform(put("/towns/" + townId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTownDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value("The town with the specified ID was successfully updated."))
                .andDo(print());
    }

    @Test
    void updateTownByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        TownDto newTownDto = new TownDto();
        newTownDto.setName("Moscow");

        mockMvc.perform(put("/towns/" + townId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTownDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value("The town with the specified ID was not found."))
                .andExpect(jsonPath("result").value(false))
                .andDo(print());
    }

    private UUID getNewTownIdFromDatabase(TownDto townDto) throws Exception {
        MvcResult mvcResultTown = mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultTown.getResponse().getContentAsString(), ResponseDto.class).getId();
    }
}