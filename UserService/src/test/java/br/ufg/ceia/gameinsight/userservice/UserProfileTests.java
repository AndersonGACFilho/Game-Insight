package br.ufg.ceia.gameinsight.userservice;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.dtos.UserProfileDto;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import br.ufg.ceia.gameinsight.userservice.services.SequenceGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for user profile-related operations in the User Service.
 *
 * <p>This class tests the user profile functionalities, including registration,
 * authentication, fetching user profile, and updating user profile.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserProfileTests {

    /**
     * Logger for logging test information and results.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserProfileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    private User user;
    private String jwtToken;

    /**
     * Initializes a test user and authenticates it to obtain a JWT token for subsequent requests.
     *
     * <p>This method creates and registers a new user, then logs in to retrieve the JWT token
     * required for authenticated endpoints.</p>
     *
     * @throws Exception if any error occurs during setup
     */
    @BeforeAll
    public void setup() throws Exception {
        // Create a user with a unique email
        user = new User();
        user.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user.setEmail("TestUser" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "@example.com");
        String userPassword = "password";
        user.setPassword(userPassword);
        user.setName("Test User");
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setPhoneNumber("123");
        user.setProfile(userProfile);

        // Register the user
        String registerResult = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()) // Expecting HTTP 201 Created
                .andReturn()
                .getResponse()
                .getContentAsString();

        logger.info("Register response: {}", registerResult);

        // Authenticate the user to get the JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(userPassword);

        String loginResult = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andReturn()
                .getResponse()
                .getContentAsString();

        logger.info("Login response: {}", loginResult);
        jwtToken = loginResult.substring(loginResult.indexOf(":") + 2, loginResult.length() - 2);
        logger.info("JWT Token: {}", jwtToken);
    }

    /**
     * Tests the retrieval of the authenticated user's profile.
     *
     * <p>Uses the JWT token obtained during setup to access the user's profile information.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(1)
    public void testGetUserProfile() throws Exception {
        // Send GET request to retrieve user profile
        mockMvc.perform(get("/users/me/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(jsonPath("$.firstName").value(user.getProfile().getFirstName())) // Verify first name
                .andExpect(jsonPath("$.lastName").value(user.getProfile().getLastName())) // Verify last name
                .andExpect(jsonPath("$.phoneNumber").value(user.getProfile().getPhoneNumber())); // Verify phone number
    }

    /**
     * Tests updating the authenticated user's profile.
     *
     * <p>Updates the user's profile with new information and verifies that the changes
     * are correctly persisted.</p>
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @Order(2)
    public void testUpdateUserProfile() throws Exception {
        // Create a new UserProfileDto with updated information
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setFirstName("Updated");
        updatedProfile.setLastName("User");

        // Parse the birthdate to set in the updated profile
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date birthdate = dateFormat.parse("2000-01-01T00:00:00.000Z");
        updatedProfile.setBirthdate(birthdate);
        updatedProfile.setPhoneNumber("456");

        // Send PUT request to update user profile
        mockMvc.perform(put("/users/me/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(jsonPath("$.firstName").value(updatedProfile.getFirstName())) // Verify updated first name
                .andExpect(jsonPath("$.lastName").value(updatedProfile.getLastName())) // Verify updated last name
                .andExpect(jsonPath("$.phoneNumber").value(updatedProfile.getPhoneNumber())); // Verify updated phone number

        // Verify that the profile was updated in the repository
        UserProfile savedProfile = userRepository.findByEmail(user.getEmail()).orElseThrow().getProfile();
        Assertions.assertEquals(updatedProfile.getFirstName(), savedProfile.getFirstName());
        Assertions.assertEquals(updatedProfile.getLastName(), savedProfile.getLastName());
        Assertions.assertEquals(updatedProfile.getPhoneNumber(), savedProfile.getPhoneNumber());
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
