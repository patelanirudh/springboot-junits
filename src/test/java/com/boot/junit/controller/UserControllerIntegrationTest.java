package com.boot.junit.controller;

import com.boot.junit.model.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Default webEnvironment = SpringBootTest.WebEnvironment.MOCK. This does not start embedded server and
// does not load all beans in spring context, only web layer (same as MockMvc).
// override application.properties via ('properties' field or RANDOM PORT)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"server.port=9082", "hostname=192.168.0.2"})
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.yml", properties = "server.port=9084")
// higher preference properties > application-test.prop > application.prop
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    private JSONObject userDetailsRequestJson;

    // This is an alternate to RestTemplate, can use both.
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setupTest() throws JSONException {
        userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Shilpi");
        userDetailsRequestJson.put("lastName", "Patel");
        userDetailsRequestJson.put("email", "shilpi@pagli.com");
        userDetailsRequestJson.put("password", "12345678");
        userDetailsRequestJson.put("repeatPassword", "12345678");
    }

    @AfterAll
    void cleanup() { // now does not have to be static since "TestInstance.Lifecycle.PER_CLASS"
        System.out.println("--- @AfterAll method is invoked ---");
        userDetailsRequestJson = null;
    }

    @Test
    void contextLoads() {
        System.out.println("Server port in use " + serverPort);
        System.out.println("LocalServerPort in use " + localServerPort);
    }

    @DisplayName("Create User")
    @Test
    @Order(1)
    void testCreateUser_whenValidDetailsGiven_returnUserCreated() throws JSONException {
        // Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(userDetailsRequestJson.toString(), httpHeaders);

        // Act
        // String responseString = testRestTemplate.postForEntity("/users", requestEntity, String.class);

        ResponseEntity<UserRest> createdUser = testRestTemplate.postForEntity("/users", requestEntity, UserRest.class);
        UserRest returnedUser = createdUser.getBody();
        System.out.println("Returned Created User : " + returnedUser);

        // Assert
        Assertions.assertNotNull(returnedUser, "UserRest should not be null");
        Assertions.assertEquals(HttpStatus.OK, createdUser.getStatusCode(), "HttpStatus OK should be returned");
        Assertions.assertEquals(userDetailsRequestJson.get("firstName"), returnedUser.getFirstName(), "FirstName should match");
    }

    @DisplayName("Get Created User via Request Param : Email")
    @Test
    @Order(2)
    void testGetUser_whenValidRequestParamInputGiven_returnStoredUser() throws JSONException {
        // Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        // httpHeaders.set("Accept", "application/json");
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity requestEntity = new HttpEntity(httpHeaders);
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("emailName", "shilpi@pagli.com");

        // Act
        ResponseEntity<UserRest> returnedUser = testRestTemplate.exchange("/users/email?emailName={emailName}",
                HttpMethod.GET,
                requestEntity,
                UserRest.class, // for List ParameterizedTypeReference<List<UserRest>>
                requestParam);
        System.out.println("Rest Template userRest : " + returnedUser.getBody().toString());
        UserRest userRest = returnedUser.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, returnedUser.getStatusCode(), "HttpStatus OK should be returned");
        Assertions.assertNotNull(userRest, "UserRest should not be null");
        Assertions.assertEquals(userDetailsRequestJson.get("firstName"), userRest.getFirstName(), "FirstName should match");
    }

    @DisplayName("Get Created User via Path Variable : LastName")
    @Test
    @Order(3)
    void testGetUser_whenValidPathVariableInputGiven_returnStoredUser() throws JSONException {
        // Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        // httpHeaders.set("Accept", "application/json");
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity requestEntity = new HttpEntity(httpHeaders);
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("lastName", "Patel");

        // Act
        ResponseEntity<UserRest> returnedUser = testRestTemplate.exchange("/users/names/{lastName}",
                HttpMethod.GET,
                requestEntity,
                UserRest.class, // for List ParameterizedTypeReference<List<UserRest>>
                requestParam);
        System.out.println("Rest Template userRest : " + returnedUser.getBody().toString());
        UserRest userRest = returnedUser.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, returnedUser.getStatusCode(), "HttpStatus OK should be returned");
        Assertions.assertNotNull(userRest, "UserRest should not be null");
        Assertions.assertEquals(userDetailsRequestJson.get("firstName"), userRest.getFirstName(), "FirstName should match");
    }
}
