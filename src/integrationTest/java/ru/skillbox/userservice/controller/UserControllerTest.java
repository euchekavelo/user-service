package ru.skillbox.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skillbox.userservice.config.PostgreSQLContainerConfig;
import ru.skillbox.userservice.dto.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PostgreSQLContainerConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserSuccess() throws Exception {
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Petrov Petr Ivanovich");
        shortUserDto.setEmail("petrov_test@gmail.com");
        shortUserDto.setSex("MALE");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("message").value("User successfully created."))
                .andDo(print());
    }

    @Test
    void createUserSuccessThrowSQLExceptionForUniqueField() throws Exception {
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Varlamov Petr Ivanovich");
        shortUserDto.setEmail("varlamov_test@gmail.com");
        shortUserDto.setSex("MALE");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(content().string(Matchers.containsString("duplicate key value")))
                .andDo(print());
    }

    @Test
    void getUserByIdSuccess() throws Exception {
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Sidorov Ivan Ivanovich");
        shortUserDto.setEmail("sidorov_test@gmail.com");
        shortUserDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserDto);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("sidorov_test@gmail.com"))
                .andDo(print());
    }

    @Test
    void getUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void updateUserByIdSuccess() throws Exception {
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Komov Ivan Ivanovich");
        shortUserDto.setEmail("komov_test@gmail.com");
        shortUserDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserDto);

        TownDto townDto = new TownDto();
        townDto.setName("Test town");

        UUID townId = getNewTownIdFromDatabase(townDto);

        UserDto userDto = new UserDto();
        userDto.setFullname("Komov Ivan Ivanovich");
        userDto.setEmail("komov123_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user was successfully updated."))
                .andDo(print());
    }

    @Test
    void updateUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        TownDto townDto = new TownDto();
        townDto.setName("Test town");

        UUID townId = getNewTownIdFromDatabase(townDto);

        UserDto userDto = new UserDto();
        userDto.setFullname("Ivanov Ivan Ivanovich");
        userDto.setEmail("invanovs_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void updateUserByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b11bba");

        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Plotnikov Ivan Ivanovich");
        shortUserDto.setEmail("plotnikov_test@gmail.com");
        shortUserDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserDto);

        UserDto userDto = new UserDto();
        userDto.setFullname("Plotnikov Ivan Ivanovich");
        userDto.setEmail("plotnikov123_test@gmail.com");
        userDto.setSex("MALE");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userDto.setPhone("+79999999999");
        userDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The town with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void deleteUserByIdSuccess() throws Exception {
        ShortUserDto shortUserDto = new ShortUserDto();
        shortUserDto.setFullname("Smirnov Ivan Ivanovich");
        shortUserDto.setEmail("smirnov_test@gmail.com");
        shortUserDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserDto);
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user with the specified ID was successfully deleted."))
                .andDo(print());
    }

    @Test
    void deleteUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void subscribeToUserSuccess() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Aleev Ivan Ivanovich");
        sourceShortUserDto.setEmail("aleev_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        ShortUserDto destinationShortUserDto = new ShortUserDto();
        destinationShortUserDto.setFullname("Vilkov Ivan Ivanovich");
        destinationShortUserDto.setEmail("vilkov_test@gmail.com");
        destinationShortUserDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The target user was successfully subscribed."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserNotFoundExceptionWhenSourceUserNotExists() throws Exception {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        ShortUserDto destinationShortUserDto = new ShortUserDto();
        destinationShortUserDto.setFullname("Valeev Ivan Ivanovich");
        destinationShortUserDto.setEmail("valeev_test@gmail.com");
        destinationShortUserDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The source user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserNotFoundExceptionWhenDestinationUserNotExists() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Frolov Ivan Ivanovich");
        sourceShortUserDto.setEmail("frolov_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The destination user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserSubscriptionExceptionWhenSubscribeToYourself() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Maneev Ivan Ivanovich");
        sourceShortUserDto.setEmail("maneev_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(sourceUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user cannot subscribe to himself."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserSubscriptionExceptionWhenSubscriptionExists() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Akimov Ivan Ivanovich");
        sourceShortUserDto.setEmail("akimov_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        ShortUserDto destinationShortUserDto = new ShortUserDto();
        destinationShortUserDto.setFullname("Aluev Ivan Ivanovich");
        destinationShortUserDto.setEmail("aluev_test@gmail.com");
        destinationShortUserDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("A subscription to the destination user already exists."))
                .andDo(print());
    }

    @Test
    void unsubscribeFromUserSuccess() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Milov Ivan Ivanovich");
        sourceShortUserDto.setEmail("milov_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        ShortUserDto destinationShortUserDto = new ShortUserDto();
        destinationShortUserDto.setFullname("Venkov Ivan Ivanovich");
        destinationShortUserDto.setEmail("venkov_test@gmail.com");
        destinationShortUserDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/unsubscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The destination user has been unsubscribed successfully."))
                .andDo(print());
    }

    @Test
    void unsubscribeFromThrowUserSubscriptionExceptionUserWhenUserUnsubscribeYourself() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Nikitov Nikita Ivanovich");
        sourceShortUserDto.setEmail("nikitov_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(sourceUserId);

        mockMvc.perform(post("/users/unsubscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user cannot unsubscribe to himself."))
                .andDo(print());
    }

    @Test
    void unsubscribeFromThrowUserSubscriptionExceptionUserWhenUserUnsubscribeNotExists() throws Exception {
        UUID sourceUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID destinationUserId = UUID.fromString("13cfa0c0-1fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/unsubscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("No subscription has been identified in relation to the destination user."))
                .andDo(print());
    }

    @Test
    void addUserToGroupSuccess() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupDto);

        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Teleev Nikita Ivanovich");
        sourceShortUserDto.setEmail("teleev_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserDto);

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user has been successfully added to the group."))
                .andDo(print());
    }

    @Test
    void addUserToGroupThrowUserNotFoundException() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupDto);
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void addUserToGroupThrowGroupNotFoundException() throws Exception {
        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Eleseev Nikita Ivanovich");
        sourceShortUserDto.setEmail("eleseev_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserDto);
        UUID groupId = UUID.fromString("15cfa0c0-2fe3-47d1-116b-761e59b67ccd");

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("The group with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void deleteUserFromGroupSuccess() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test Moscow Group");
        UUID groupId = getNewGroupIdFromDatabase(groupDto);

        ShortUserDto sourceShortUserDto = new ShortUserDto();
        sourceShortUserDto.setFullname("Evseev Nikita Ivanovich");
        sourceShortUserDto.setEmail("evseev_test@gmail.com");
        sourceShortUserDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserDto);

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user has been successfully removed from the group."))
                .andDo(print());;
    }

    @Test
    void deleteUserFromGroupThrowUserGroupException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("13cfa0c0-1fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(delete("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message")
                        .value("Cannot remove a user from a group."))
                .andDo(print());;
    }

    private UUID getNewGroupIdFromDatabase(GroupDto groupDto) throws Exception {
        MvcResult mvcResultGroup = mockMvc.perform(post("/groups")
                        .content(objectMapper.writeValueAsString(groupDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultGroup.getResponse().getContentAsString(), ResponseDto.class).getId();
    }

    private UUID getNewUserIdFromDatabase(ShortUserDto shortUserDto) throws Exception {
        MvcResult mvcResultPerson = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultPerson.getResponse().getContentAsString(), ResponseDto.class).getId();
    }

    private UUID getNewTownIdFromDatabase(TownDto townDto) throws Exception {
        MvcResult mvcResultTown = mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultTown.getResponse().getContentAsString(), ResponseDto.class).getId();
    }
}