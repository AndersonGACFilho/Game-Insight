package br.ufg.ceia.gameinsight.userservice;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for user registration and login-related operations in the User Service.
 *
 * <p>This class tests the functionalities for registering a new user, authenticating the user,
 * fetching the authenticated user's details, and retrieving all users.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginAndRegisterTests {

    /**
     * Logger for logging test information and results.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginAndRegisterTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private final String userPassword = "password";
    private String jwtToken;

    /**
     * Initializes a test user with a unique email before all tests are executed.
     */
    @BeforeAll
    public void setup() {
        user = new User();
        user.setEmail("TestUser" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                "@example.com");
        user.setPassword(userPassword);
        user.setName("Test User");
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setPhoneNumber("123");
        user.setProfile(userProfile);
    }

    /**
     * Tests the registration of a new user.
     *
     * <p>This test registers a new user and verifies that the user is successfully created in the database.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(1)
    public void testRegisterUser() throws Exception {
        logger.info("Registering user: {}", user);

        // Register the user and get the result
        String result = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()) // Expecting HTTP 201 Created
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Retrieve the user from the repository to verify creation
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new Exception("User not found"));
    }

    /**
     * Tests the registration of a new user with an existing email.
     * <p>
     * This test attempts to register a new user with an email that already exists in the database
     * and verifies that the appropriate error response is returned.
     * </p>
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(2)
    public void testRegisterUser_ExistingEmail() throws Exception {
        // Attempt to register the user with an existing email
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict()); // Expecting HTTP 409 Conflict
    }

    /**
     * Tests failed user registration with missing required fields.
     *
     * <p>This test attempts to register a user without providing required fields and verifies
     * that the appropriate validation errors are returned.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(3)
    public void testRegisterUser_MissingFields() throws Exception {
        User invalidUser = new User();
        invalidUser.setEmail(""); // Missing email
        invalidUser.setPassword(""); // Missing password
        invalidUser.setName(""); // Missing name

        // Attempt to register the user with missing fields
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()); // Expecting HTTP 400 Bad Request
    }

    /**
     * Tests the authentication of a registered user.
     *
     * <p>This test logs in the user and retrieves a JWT token for authenticated requests.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(4)
    public void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(userPassword);

        // Authenticate the user and get the JWT token
        jwtToken = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the JWT token from the response
        jwtToken = jwtToken.substring(jwtToken.indexOf(":") + 2, jwtToken.length() - 2);
        logger.info("JWT Token: {}", jwtToken);
    }


    /**
     * Tests failed authentication with invalid credentials.
     *
     * <p>This test attempts to authenticate a user with invalid credentials and verifies
     * that the appropriate error response is returned.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(5)
    public void testAuthenticateUser_InvalidCredentials() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest();
        invalidLoginRequest.setEmail("invalid@example.com");
        invalidLoginRequest.setPassword("wrongpassword");

        // Attempt to authenticate with invalid credentials
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isUnauthorized()); // Expecting HTTP 401 Unauthorized
    }

    /**
     * Tests retrieval of the authenticated user's details.
     *
     * <p>This test uses the JWT token to fetch the authenticated user's information.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(6)
    public void testGetAuthenticatedUser() throws Exception {
        logger.info("JWT Token: {}", jwtToken);

        // Send GET request to retrieve authenticated user's details
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(jsonPath("$.email").value(user.getEmail())); // Verify email
    }

    /**
     * Tests retrieval of all users.
     *
     * <p>This test uses the JWT token to fetch a list of all users in the system.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(7)
    public void testGetAllUsers() throws Exception {
        // Send GET request to retrieve all users
        mockMvc.perform(get("/users/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(jsonPath("$.length()").isNotEmpty()); // Verify that the response is not empty
    }

    /**
     * Cleans up the test user from the database after all tests have been executed.
     */
    @AfterAll
    public void tearDown() {
        // Remove the test user from the repository
        userRepository.findByEmail(this.user.getEmail()).ifPresent(userRepository::delete);
    }
}
