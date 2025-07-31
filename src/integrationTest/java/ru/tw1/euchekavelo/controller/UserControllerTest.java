package ru.tw1.euchekavelo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tw1.euchekavelo.config.PostgreSQLContainerConfig;
import ru.tw1.euchekavelo.config.S3Configuration;
import ru.tw1.euchekavelo.dto.request.*;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.dto.response.UserPhotoResponseDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.*;

@SpringBootTest(classes = {PostgreSQLContainerConfig.class, S3Configuration.class})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static File correctFile;
    private static File incorrectFile;

    @BeforeAll
    static void beforeAll() {
        correctFile = new File("src/integrationTest/resources/files/correct_file.png");
        incorrectFile = new File("src/integrationTest/resources/files/incorrect_file.txt");
    }

    @Test
    void createUserSuccess() throws Exception {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Petrov Petr Ivanovich");
        shortUserRequestDto.setEmail("petrov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(print());
    }

    @Test
    void createUserSuccessThrowSQLExceptionForUniqueField() throws Exception {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Varlamov Petr Ivanovich");
        shortUserRequestDto.setEmail("varlamov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(content().string(Matchers.containsString("duplicate key value")))
                .andDo(print());
    }

    @Test
    void getUserByIdSuccess() throws Exception {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Sidorov Ivan Ivanovich");
        shortUserRequestDto.setEmail("sidorov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserRequestDto);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("sidorov_test@gmail.com"))
                .andDo(print());
    }

    @Test
    void getUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void updateUserByIdSuccess() throws Exception {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Komov Ivan Ivanovich");
        shortUserRequestDto.setEmail("komov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserRequestDto);

        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Test town");

        UUID townId = getNewTownIdFromDatabase(townRequestDto);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Komov Ivan Ivanovich");
        userRequestDto.setEmail("komov123_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Test town");

        UUID townId = getNewTownIdFromDatabase(townRequestDto);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Ivanov Ivan Ivanovich");
        userRequestDto.setEmail("invanovs_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void updateUserByIdThrowTownNotFoundException() throws Exception {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b11bba");

        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Plotnikov Ivan Ivanovich");
        shortUserRequestDto.setEmail("plotnikov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserRequestDto);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFullname("Plotnikov Ivan Ivanovich");
        userRequestDto.setEmail("plotnikov123_test@gmail.com");
        userRequestDto.setSex("MALE");
        userRequestDto.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDto.setPhone("+79999999999");
        userRequestDto.setTownId(townId);

        mockMvc.perform(put("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void deleteUserByIdSuccess() throws Exception {
        ShortUserRequestDto shortUserRequestDto = new ShortUserRequestDto();
        shortUserRequestDto.setFullName("Smirnov Ivan Ivanovich");
        shortUserRequestDto.setEmail("smirnov_test@gmail.com");
        shortUserRequestDto.setSex("MALE");

        UUID userId = getNewUserIdFromDatabase(shortUserRequestDto);
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteUserByIdThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void subscribeToUserSuccess() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Aleev Ivan Ivanovich");
        sourceShortUserRequestDto.setEmail("aleev_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        ShortUserRequestDto destinationShortUserRequestDto = new ShortUserRequestDto();
        destinationShortUserRequestDto.setFullName("Vilkov Ivan Ivanovich");
        destinationShortUserRequestDto.setEmail("vilkov_test@gmail.com");
        destinationShortUserRequestDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserRequestDto);

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

        ShortUserRequestDto destinationShortUserRequestDto = new ShortUserRequestDto();
        destinationShortUserRequestDto.setFullName("Valeev Ivan Ivanovich");
        destinationShortUserRequestDto.setEmail("valeev_test@gmail.com");
        destinationShortUserRequestDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserRequestDto);

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value("The source user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserNotFoundExceptionWhenDestinationUserNotExists() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Frolov Ivan Ivanovich");
        sourceShortUserRequestDto.setEmail("frolov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        UUID destinationUserId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        UserSubscriptionDto userSubscriptionDto = new UserSubscriptionDto();
        userSubscriptionDto.setSourceUserId(sourceUserId);
        userSubscriptionDto.setDestinationUserId(destinationUserId);

        mockMvc.perform(post("/users/subscription")
                        .content(objectMapper.writeValueAsString(userSubscriptionDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value("The destination user with the specified ID was not found."))
                .andDo(print());
    }

    @Test
    void subscribeToUserThrowUserSubscriptionExceptionWhenSubscribeToYourself() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Maneev Ivan Ivanovich");
        sourceShortUserRequestDto.setEmail("maneev_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

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
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Akimov Ivan Ivanovich");
        sourceShortUserRequestDto.setEmail("akimov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        ShortUserRequestDto destinationShortUserRequestDto = new ShortUserRequestDto();
        destinationShortUserRequestDto.setFullName("Aluev Ivan Ivanovich");
        destinationShortUserRequestDto.setEmail("aluev_test@gmail.com");
        destinationShortUserRequestDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserRequestDto);

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
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Milov Ivan Ivanovich");
        sourceShortUserRequestDto.setEmail("milov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        ShortUserRequestDto destinationShortUserRequestDto = new ShortUserRequestDto();
        destinationShortUserRequestDto.setFullName("Venkov Ivan Ivanovich");
        destinationShortUserRequestDto.setEmail("venkov_test@gmail.com");
        destinationShortUserRequestDto.setSex("MALE");
        UUID destinationUserId = getNewUserIdFromDatabase(destinationShortUserRequestDto);

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
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Nikitov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("nikitov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID sourceUserId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

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
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);

        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Teleev Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("teleev_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user has been successfully added to the group."))
                .andDo(print());
    }

    @Test
    void addUserToGroupThrowUserNotFoundException() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(USER_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void addUserToGroupThrowGroupNotFoundException() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Eleseev Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("eleseev_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        UUID groupId = UUID.fromString("15cfa0c0-2fe3-47d1-116b-761e59b67ccd");

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage()))
                .andDo(print());
    }

    @Test
    void deleteUserFromGroupSuccess() throws Exception {
        GroupRequestDto groupRequestDto = new GroupRequestDto();
        groupRequestDto.setName("Test Moscow Group");
        UUID groupId = getNewGroupIdFromDatabase(groupRequestDto);

        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Evseev Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("evseev_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);

        mockMvc.perform(post("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("The user has been successfully removed from the group."))
                .andDo(print());;
    }

    @Test
    void deleteUserFromGroupThrowUserGroupNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b67ccd");
        UUID groupId = UUID.fromString("13cfa0c0-1fe3-47d9-916b-761e59b67ccd");

        mockMvc.perform(delete("/users/" + userId + "/groups/" + groupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value("Cannot remove a user from a group."))
                .andDo(print());;
    }

    @Test
    void createUserPhotoSuccess() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Testov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("testov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        MockMultipartFile multipartFile = getMockMultipartFile(correctFile);

        mockMvc.perform(multipart(HttpMethod.POST, "/users/" + userId + "/photos")
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").isNotEmpty())
                .andDo(print());
    }

    @Test
    void createUserPhotoThrowUserNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b89ccd");
        MockMultipartFile multipartFile = getMockMultipartFile(correctFile);

        mockMvc.perform(multipart(HttpMethod.POST, "/users/" + userId + "/photos")
                        .file(multipartFile))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createUserPhotoThrowIncorrectFileContentException() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Pestov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("pestov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(multipart(HttpMethod.POST, "/users/" + userId + "/photos")
                        .file(multipartFile))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createUserPhotoThrowIncorrectFileFormatException() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Vestov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("vestov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        MockMultipartFile multipartFile = getMockMultipartFile(incorrectFile);

        mockMvc.perform(multipart(HttpMethod.POST, "/users/" + userId + "/photos")
                        .file(multipartFile))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void getUserPhotoByIdSuccess() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Xestov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("xestov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        UUID photoId = getNewUserPhotoFromDatabase(correctFile, userId);

        mockMvc.perform(get("/users/" + userId + "/photos/" + photoId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getUserPhotoByIdThrowPhotoNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-761e59b89ccd");
        UUID photoId = UUID.fromString("11cfa0c0-2fe3-47d9-916b-761e59b11ccd");

        mockMvc.perform(get("/users/" + userId + "/photos/" + photoId))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void deleteUserPhotoByIdSuccess() throws Exception {
        ShortUserRequestDto sourceShortUserRequestDto = new ShortUserRequestDto();
        sourceShortUserRequestDto.setFullName("Festov Nikita Ivanovich");
        sourceShortUserRequestDto.setEmail("festov_test@gmail.com");
        sourceShortUserRequestDto.setSex("MALE");
        UUID userId = getNewUserIdFromDatabase(sourceShortUserRequestDto);
        UUID photoId = getNewUserPhotoFromDatabase(correctFile, userId);

        mockMvc.perform(delete("/users/" + userId + "/photos/" + photoId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteUserPhotoByIdThrowPhotoNotFoundException() throws Exception {
        UUID userId = UUID.fromString("09cfa0c0-2fe3-47d9-916b-711e59b89ccd");
        UUID photoId = UUID.fromString("33cfa0c0-2fe3-47d9-916b-771e59b11ccd");

        mockMvc.perform(delete("/users/" + userId + "/photos/" + photoId))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private UUID getNewGroupIdFromDatabase(GroupRequestDto groupRequestDto) throws Exception {
        MvcResult mvcResultGroup = mockMvc.perform(post("/groups")
                        .content(objectMapper.writeValueAsString(groupRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultGroup.getResponse().getContentAsString(), ResponseDto.class).getId();
    }

    private UUID getNewUserIdFromDatabase(ShortUserRequestDto shortUserRequestDto) throws Exception {
        MvcResult mvcResultPerson = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(shortUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultPerson.getResponse().getContentAsString(), ResponseDto.class).getId();
    }

    private UUID getNewTownIdFromDatabase(TownRequestDto townRequestDto) throws Exception {
        MvcResult mvcResultTown = mockMvc.perform(post("/towns")
                        .content(objectMapper.writeValueAsString(townRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mvcResultTown.getResponse().getContentAsString(), ResponseDto.class).getId();
    }

    private UUID getNewUserPhotoFromDatabase(File file, UUID userId) throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile(file);
        MvcResult mvcResultPhoto = mockMvc.perform(multipart(HttpMethod.POST, "/users/" + userId + "/photos")
                        .file(multipartFile)).andReturn();

        return objectMapper.readValue(mvcResultPhoto.getResponse().getContentAsString(), UserPhotoResponseDto.class)
                .getId();
    }

    private MockMultipartFile getMockMultipartFile(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            String contentType = MediaTypeFactory.getMediaType(file.getName()).toString();

            return new MockMultipartFile("file", file.getName(), contentType, inputStream);
        }
    }
}