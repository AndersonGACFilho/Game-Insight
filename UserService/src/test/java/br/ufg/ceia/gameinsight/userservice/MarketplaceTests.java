package br.ufg.ceia.gameinsight.userservice;

import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceType;
import br.ufg.ceia.gameinsight.userservice.controllers.UserController;
import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.dtos.MarketplaceProfileDto;
import br.ufg.ceia.gameinsight.userservice.dtos.UserDto;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for marketplace profile-related operations in the User Service.
 *
 * <p>This class tests functionalities related to adding, removing, and updating
 * marketplace profiles for users.</p>
 */
class MarketplaceTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User mockUser;
    private MarketplaceProfileDto mockMarketplaceProfileDto;
    private MarketplaceProfile mockMarketplaceProfile;

    /**
     * Sets up the test environment before each test.
     *
     * <p>This method initializes mock objects and configures the security context
     * for authentication.</p>
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock user
        mockUser = new User();
        mockUser.setId(1L); // Long ID type
        mockUser.setEmail("test@example.com");
        mockUser.setMarketplaceProfiles(new ArrayList<>());

        // Set up mock marketplace profile DTO
        mockMarketplaceProfileDto = new MarketplaceProfileDto();
        mockMarketplaceProfileDto.setUsername("gamer123");
        mockMarketplaceProfileDto.setMarketplaceType(MarketplaceType.STEAM);

        // Set up mock marketplace profile
        mockMarketplaceProfile = new MarketplaceProfile();
        mockMarketplaceProfile.setUsername("gamer123");
        mockMarketplaceProfile.setMarketplaceType(MarketplaceType.STEAM);
        mockMarketplaceProfile.setGames(new ArrayList<>());
        mockMarketplaceProfile.setLoginToken("token123");

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Tests successful addition of a marketplace profile.
     *
     * <p>This test verifies that a marketplace profile can be successfully added to a user.</p>
     */
    @Test
    void testAddMarketplaceProfile_Success() {
        // Arrange: Set up mock responses for userService methods
        when(userService.getUser()).thenReturn(mockUser);
        when(userService.addMarketplaceProfile(any(MarketplaceProfile.class))).thenReturn(mockUser);

        // Act: Call the controller method to add a marketplace profile
        ResponseEntity<UserDto> response = userController.addMarketplaceProfile(mockMarketplaceProfileDto);

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).addMarketplaceProfile(any(MarketplaceProfile.class));
    }

    /**
     * Tests addition of a marketplace profile when the user is not found.
     *
     * <p>This test verifies that the appropriate response is returned when the user
     * is not found during the addition of a marketplace profile.</p>
     */
    @Test
    void testAddMarketplaceProfile_UserNotFound() {
        // Arrange: Set up mock response for userService.getUser to throw an exception
        when(userService.getUser()).thenThrow(new ResourceNotFoundException("User not found"));

        // Act: Call the controller method to add a marketplace profile
        ResponseEntity<UserDto> response = userController.addMarketplaceProfile(mockMarketplaceProfileDto);

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found
        verify(userService, times(1)).getUser();
        verify(userService, times(0)).addMarketplaceProfile(any(MarketplaceProfile.class));
    }

    /**
     * Tests successful removal of a marketplace profile.
     *
     * <p>This test verifies that a marketplace profile can be successfully removed from a user.</p>
     */
    @Test
    void testRemoveMarketplaceProfile_Success() {
        // Arrange: Set up mock response for userService.removeMarketplaceProfile
        when(userService.removeMarketplaceProfile(any(String.class))).thenReturn(mockUser);

        // Act: Call the controller method to remove a marketplace profile
        ResponseEntity<Void> response = userController.removeMarketplaceProfile(
                mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    /**
     * Tests removal of a marketplace profile when the user is not found.
     *
     * <p>This test verifies that the appropriate response is returned when the user
     * is not found during the removal of a marketplace profile.</p>
     */
    @Test
    void testRemoveMarketplaceProfile_UserNotFound() {
        // Arrange: Set up mock response for userService.removeMarketplaceProfile to throw an exception
        when(userService.removeMarketplaceProfile(any(String.class))).thenThrow(
                new ResourceNotFoundException("User not found"));

        // Act: Call the controller method to remove a marketplace profile
        ResponseEntity<Void> response = userController.removeMarketplaceProfile(mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found
        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    /**
     * Tests removal of a marketplace profile when the profile is not found.
     *
     * <p>This test verifies that the appropriate response is returned when the
     * marketplace profile is not found during the removal process.</p>
     */
    @Test
    void testRemoveMarketplaceProfile_ProfileNotFound() {
        // Arrange: Set up mock response for userService.removeMarketplaceProfile to throw an exception
        when(userService.removeMarketplaceProfile(any(String.class))).thenThrow(
                new ResourceNotFoundException("Marketplace profile not found"));

        // Act: Call the controller method to remove a marketplace profile
        ResponseEntity<Void> response = userController.removeMarketplaceProfile(mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when profile is not found
        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    /**
     * Tests successful update of a marketplace profile.
     *
     * <p>This test verifies that a marketplace profile can be successfully updated for a user.</p>
     */
    @Test
    void testUpdateMarketplaceProfile_Success() {
        // Arrange: Set up mock response for userService.updateMarketplaceProfile
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfile.class))).thenReturn(
                mockUser);

        // Act: Call the controller method to update a marketplace profile
        ResponseEntity<Void> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfile,
                mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Tests update of a marketplace profile when the user is not found.
     *
     * <p>This test verifies that the appropriate response is returned when the user
     * is not found during the update of a marketplace profile.</p>
     */
    @Test
    void testUpdateMarketplaceProfile_UserNotFound() {
        // Arrange: Set up mock response for userService.updateMarketplaceProfile to throw an exception
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfile.class))).thenThrow(
                new ResourceNotFoundException("User not found"));

        // Act: Call the controller method to update a marketplace profile
        ResponseEntity<Void> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfile,
                mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found
        verify(userService, times(1)).updateMarketplaceProfile(any(String.class), any(
                MarketplaceProfile.class));
    }

    /**
     * Tests update of a marketplace profile when the profile is not found.
     *
     * <p>This test verifies that the appropriate response is returned when the
     * marketplace profile is not found during the update process.</p>
     */
    @Test
    void testUpdateMarketplaceProfile_ProfileNotFound() {
        // Arrange: Set up mock response for userService.updateMarketplaceProfile to throw an exception
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfile.class))).thenThrow(
                new ResourceNotFoundException("Marketplace profile not found"));

        // Act: Call the controller method to update a marketplace profile
        ResponseEntity<Void> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfile,
                mockMarketplaceProfileDto.getUsername());

        // Assert: Verify the response and interactions with the userService
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when profile is not found
        verify(userService, times(1)).updateMarketplaceProfile(any(String.class), any(MarketplaceProfile.class));
    }
}
