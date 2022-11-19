package com.boot.junit.controller;


import com.boot.junit.model.UserDetailsRequestModel;
import com.boot.junit.model.UserRest;
import com.boot.junit.service.UsersService;
import com.boot.junit.service.UsersServiceImpl;
import com.boot.junit.shared.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
// if we have Spring Security : tell Spring Security to autoconfigure create MockWebMvc but without security filters
// Present by default in above WebMvcTest annotation. We may also excludeSpringSecurity configuration above.
// @AutoConfigureMockMvc(addFilters = false)
@MockBean(classes = {UsersServiceImpl.class}) // can provide all impl's of userService interface
public class UsersControllerWebLayerTest {

    private UserDetailsRequestModel requestModel;
    @Autowired
    private MockMvc mockMvc;

    // mocks the interface, if there is only 1 IMPLEMENTATION CLASS. Loads the BEAN in Spring's app context
    // Else shift thus to class and provide list of all IMPLEMENTATION CLASSES
    // @MockBean
    // UsersService userService;

    @Autowired // this is needed if MockBean is placed at Class Level (as in our case)
    UsersService usersService;

    @BeforeEach
    void setupMethod() {
        System.out.println("******** @BeforeEach setupMethod() executed ********");
        requestModel = new UserDetailsRequestModel();
        requestModel.setFirstName("Shilpi");
        requestModel.setLastName("Patel");
        requestModel.setEmail("shilpi@pagli.com");
        requestModel.setPassword("12345678");
        requestModel.setRepeatPassword("12345678");
    }

    @DisplayName("User Creation")
    @Test
    void testCreateUser_whenValidDetailsProvided_returnUserDetails() throws Exception {
        // Arrange
        // UserDetailsRequestModel has been created in @BeforeEachTestMethod()

//        UserDto userDto = new UserDto();
//        userDto.setUserId("DuumyId");
//        userDto.setFirstName("Shilpi");
//        userDto.setLastName("Patel");
//        userDto.setEmail("shilpi@pagli.com");
//        userDto.setUserId(UUID.randomUUID().toString());
        UserDto userDto = new ModelMapper().map(requestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        // mocked passing any UserDto class to userService but mocked it to return above userDto object
        when(usersService.createUser(Mockito.any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);

        // Assert
        Assertions.assertNotNull(createdUser, "User must have been created and should not have been null");
        Assertions.assertEquals(requestModel.getFirstName(), createdUser.getFirstName(), "firstName should be equal");
        Assertions.assertFalse(createdUser.getUserId().isEmpty(), "UserId should not be empty");
    }

    @DisplayName("User Creation Failed - Invalid Password")
    @Test
    void testCreateUser_whenInvalidPasswordGiven_returnBadRequest() throws Exception {
        // Arrange : set invalid password on requestModel
        requestModel.setPassword("1234567");
        requestModel.setRepeatPassword("1234567");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));
        int expectedHttpStatusCode = HttpStatus.BAD_REQUEST.value();

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(expectedHttpStatusCode, mvcResult.getResponse().getStatus(), "Incorrect HTTP Status Code Returned");
    }

    @DisplayName("User Creation Failed : Invalid FirstName")
    @Test
    void testCreateUser_whenInvalidFirstNameGiven_returnBadRequest() throws Exception {
        // Arrange
        requestModel.setFirstName("A");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));
        int expectedHttpStatusCode = HttpStatus.BAD_REQUEST.value();

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(expectedHttpStatusCode, mvcResult.getResponse().getStatus(), "Incorrect Http Status Code Returned");
    }

}
