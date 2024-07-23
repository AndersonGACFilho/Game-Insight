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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserProfileTests {

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
    private String userPassword = "password";

    private String jwtToken;

    @BeforeAll
    public void setup() {
        // Create a user
        user = new User();
        user.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user.setEmail("TestUser" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "@example.com");
        user.setPassword(userPassword);
        user.setName("Test User");
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setPhoneNumber("123");
        user.setProfile(userProfile);

        user = userRepository.save(user);

        // Authenticate the user to get the JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(userPassword);

        try {
            String result = mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            logger.info("Login response: {}", result);
            jwtToken = result.substring(result.indexOf(":") + 2, result.length() - 2);
            logger.info("JWT Token: {}", jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/users/me/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(user.getProfile().getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getProfile().getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getProfile().getPhoneNumber()));
    }

    @Test
    @Order(2)
    public void testUpdateUserProfile() throws Exception {
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setFirstName("Updated");
        updatedProfile.setLastName("User");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date birthdate = dateFormat.parse("2000-01-01T00:00:00.000Z");
        updatedProfile.setBirthdate(birthdate);
        updatedProfile.setPhoneNumber("456");

        mockMvc.perform(put("/users/me/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedProfile.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedProfile.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedProfile.getPhoneNumber()));


        UserProfile savedProfile = userRepository.findById(user.getId()).orElseThrow().getProfile();
        Assertions.assertEquals(updatedProfile.getFirstName(), savedProfile.getFirstName());
        Assertions.assertEquals(updatedProfile.getLastName(), savedProfile.getLastName());
        Assertions.assertEquals(updatedProfile.getPhoneNumber(), savedProfile.getPhoneNumber());
    }

    @AfterAll
    public void tearDown() {
        userRepository.findByEmail(this.user.getEmail()).ifPresent(userRepository::delete);
    }
}
