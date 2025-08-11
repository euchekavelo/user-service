/*package ru.tw1.euchekavelo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tw1.euchekavelo.config.PostgreSQLContainerConfig;
import ru.tw1.euchekavelo.dto.request.GroupRequestDto;
import ru.tw1.euchekavelo.dto.response.ResponseDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.GROUP_NOT_FOUND_EXCEPTION_MESSAGE;

@SpringBootTest(classes = PostgreSQLContainerConfig.class)
@AutoConfigureMockMvc
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGroupSuccess() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        mockMvc.perform(post("/groups")
                        .content(objectMapper.writeValueAsString(groupRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getGroupByIdSuccess() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);

        mockMvc.perform(get("/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("Test group"))
                .andDo(print());
    }

    @Test
    void getGroupByIdThrowGroupNotFoundException() throws Exception {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        mockMvc.perform(get("/groups/" + groupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void deleteGroupByIdSuccess() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);

        mockMvc.perform(delete("/groups/" + groupId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteGroupByIdThrowGroupNotFoundException() throws Exception {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");

        mockMvc.perform(get("/groups/" + groupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andExpect(jsonPath("result").value(false))
                .andDo(print());
    }

    @Test
    void updateGroupByIdSuccess() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);

        GroupRequestDto newGroupRequestDto = new GroupRequestDto();
        newGroupRequestDto.setName("Moscow group");

        mockMvc.perform(put("/groups/" + groupId)
                        .content(objectMapper.writeValueAsString(newGroupRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateGroupByIdThrowGroupNotFoundException() throws Exception {
        UUID groupId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b67caa");;
        GroupRequestDto newGroupRequestDto = new GroupRequestDto();
        newGroupRequestDto.setName("Moscow group");

        mockMvc.perform(put("/groups/" + groupId)
                        .content(objectMapper.writeValueAsString(newGroupRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("message")
                        .value(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andExpect(jsonPath("result").value(false))
                .andDo(print());
    }

    private UUID getNewGroupIdFromDatabase(GroupRequestDto groupRequestDto) throws Exception {
        MvcResult mvcResultGroup = mockMvc.perform(post("/groups")
                        .content(objectMapper.writeValueAsString(groupRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultGroup.getResponse().getContentAsString(), ResponseDto.class).getId();
    }
}*/