package br.ufg.ceia.gameinsight.userservice;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginAndRegisterTests {

    /**
     * Logger for the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginAndRegisterTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private String userPassword = "password";

    private String jwtToken;

    @BeforeAll
    public void setup() {
        // Create a user
        user = new User();
        user.setEmail("TestUser" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "@example.com");
        user.setPassword(userPassword);
        user.setName("Test User");
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setPhoneNumber("123");
        user.setProfile(userProfile);
    }

    @Test
    @Order(1)
    public void testRegisterUser() throws Exception {
        // Log the user details
        logger.info("User: {}", user);

        // Register the user
        String result = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + user.getEmail() + "\"," +
                                "\"password\":\"" + user.getPassword() + "\"" +
                                ",\"name\":\"" + user.getName() + "\",\"profile\":{" +
                                "\"firstName\":\"" + user.getProfile().getFirstName() + "\"" + "," +
                                "\"lastName\":\"" + user.getProfile().getLastName() + "\"," +
                                "\"birthdate\":\"" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + "\"," +
                                "\"phoneNumber\":\"" + user.getProfile().getPhoneNumber() + "\"}}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Get the user from the database
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new Exception("User not found"));
    }

    @Test
    @Order(2)
    public void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(userPassword);

        jwtToken = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + loginRequest.getEmail() + "\"," +
                                "\"password\":\"" + loginRequest.getPassword() + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Get the value of the JWT token where it is the value of the key "token"
        jwtToken = jwtToken.substring(jwtToken.indexOf(":") + 2, jwtToken.length() - 2);
        // Log the JWT token
        logger.info("JWT Token: {}", jwtToken);
    }

    @Test
    @Order(3)
    public void testGetAuthenticatedUser() throws Exception {
        logger.info("JWT Token: {}", jwtToken);
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @Order(4)
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }
    @AfterAll
    public void tearDown() {
        // Clean up test user
        userRepository.findByEmail(this.user.getEmail()).ifPresent(userRepository::delete);
    }
}
