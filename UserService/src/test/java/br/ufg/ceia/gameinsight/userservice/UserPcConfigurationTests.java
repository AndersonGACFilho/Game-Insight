package br.ufg.ceia.gameinsight.userservice;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.UserPc;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.CPU;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.DirectX;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.GPU;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.OperationalSystem;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.RAM;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for managing the user's PC configuration in the User Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserPcConfigurationTests {

    private static final Logger logger = LoggerFactory.getLogger(UserPcConfigurationTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserPc userPc;
    private final String userPassword = "password";
    private String jwtToken;

    @BeforeAll
    public void setup() throws Exception {
        user = new User();
        user.setEmail("TestUser" + System.currentTimeMillis() + "@example.com");
        user.setPassword(userPassword);
        user.setName("Test User");

        CPU cpu = new CPU("Intel Core i7");
        GPU gpu = new GPU("NVIDIA GTX 1080");
        RAM ram = new RAM(16);
        Storage storage = new Storage( 1024, "SSD");
        OperationalSystem os = new OperationalSystem("Windows 10");
        DirectX directX = new DirectX("DirectX 12");

        userPc = new UserPc(os, cpu, gpu, ram, storage, directX);

        registerUser();
        authenticateUser();
    }

    @Test
    @Order(1)
    public void testAddUserPc() throws Exception {
        logger.info("Adding PC configuration for user: {}", user.getEmail());

        mockMvc.perform(post("/users/pc")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPc)))
                .andExpect(status().isNoContent()); // Expecting HTTP 204 No Content
    }

    @Test
    @Order(2)
    public void testGetUserPc() throws Exception {
        logger.info("Retrieving PC configuration for user: {}", user.getEmail());

        mockMvc.perform(get("/users/pc")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(result -> logger.info(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.cpu.name").value("Intel Core i7"))
                .andExpect(jsonPath("$.gpu.name").value("NVIDIA GTX 1080"))
                .andExpect(jsonPath("$.ram.amount").value("16"))
                .andExpect(jsonPath("$.storage.type").value("SSD"))
                .andExpect(jsonPath("$.storage.size").value("1024"))
                .andExpect(jsonPath("$.os.name").value("Windows 10"))
                .andExpect(jsonPath("$.directX.version").value("DirectX 12"));
    }

    @Test
    @Order(3)
    public void testRemoveUserPc() throws Exception {
        logger.info("Removing PC configuration for user: {}", user.getEmail());

        mockMvc.perform(delete("/users/pc")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent()); // Expecting HTTP 204 No Content

        // Verify that the PC configuration is removed
        mockMvc.perform(get("/users/pc")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }


    private void registerUser() throws Exception  {
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
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(() ->
                new Exception("User not found"));

    }

    private void authenticateUser() throws Exception {
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
        jwtToken = jwtToken.substring(jwtToken.indexOf(":") + 2, jwtToken.length() - 2);}

    @AfterAll
    public void tearDown() {
        // Remove the test user from the repository
        userRepository.findByEmail(this.user.getEmail()).ifPresent(userRepository::delete);
    }
}
