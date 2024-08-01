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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock user and marketplace profile DTO
        mockUser = new User();
        mockUser.setId(1L); // Long ID type
        mockUser.setEmail("test@example.com");
        mockUser.setMarketplaceProfiles(new ArrayList<>());

        mockMarketplaceProfileDto = new MarketplaceProfileDto();
        mockMarketplaceProfileDto.setUsername("gamer123");
        mockMarketplaceProfileDto.setMarketplaceType(MarketplaceType.STEAM);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddMarketplaceProfile_Success() {
        // Arrange
        when(userService.getUser()).thenReturn(mockUser);
        when(userService.addMarketplaceProfile(any(MarketplaceProfile.class))).thenReturn(mockUser);

        // Act
        ResponseEntity<UserDto> response = userController.addMarketplaceProfile(mockMarketplaceProfileDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).addMarketplaceProfile(any(MarketplaceProfile.class));
    }

    @Test
    void testAddMarketplaceProfile_UserNotFound() {
        // Arrange
        when(userService.getUser()).thenThrow(new ResourceNotFoundException("User not found"));

        // Act
        ResponseEntity<UserDto> response = userController.addMarketplaceProfile(mockMarketplaceProfileDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found

        verify(userService, times(1)).getUser();
        verify(userService, times(0)).addMarketplaceProfile(any(MarketplaceProfile.class));
    }

    @Test
    void testRemoveMarketplaceProfile_Success() {
        // Arrange
        when(userService.removeMarketplaceProfile(any(String.class))).thenReturn(mockUser);

        // Act
        ResponseEntity<UserDto> response = userController.removeMarketplaceProfile(mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    @Test
    void testRemoveMarketplaceProfile_UserNotFound() {
        // Arrange
        when(userService.removeMarketplaceProfile(any(String.class))).thenThrow(new ResourceNotFoundException("User not found"));

        // Act
        ResponseEntity<UserDto> response = userController.removeMarketplaceProfile(mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found

        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    @Test
    void testRemoveMarketplaceProfile_ProfileNotFound() {
        // Arrange
        when(userService.removeMarketplaceProfile(any(String.class))).thenThrow(new ResourceNotFoundException("Marketplace profile not found"));

        // Act
        ResponseEntity<UserDto> response = userController.removeMarketplaceProfile(mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when profile is not found

        verify(userService, times(1)).removeMarketplaceProfile(any(String.class));
    }

    @Test
    void testUpdateMarketplaceProfile_Success() {
        // Arrange
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class))).thenReturn(mockUser);

        // Act
        ResponseEntity<UserDto> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfileDto, mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class));
    }

    @Test
    void testUpdateMarketplaceProfile_UserNotFound() {
        // Arrange
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class))).thenThrow(new ResourceNotFoundException("User not found"));

        // Act
        ResponseEntity<UserDto> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfileDto, mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when user is not found

        verify(userService, times(1)).updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class));
    }

    @Test
    void testUpdateMarketplaceProfile_ProfileNotFound() {
        // Arrange
        when(userService.updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class))).thenThrow(new ResourceNotFoundException("Marketplace profile not found"));

        // Act
        ResponseEntity<UserDto> response = userController.updateByMarketplaceProfileUsername(mockMarketplaceProfileDto, mockMarketplaceProfileDto.getUsername());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Expecting null body when profile is not found

        verify(userService, times(1)).updateMarketplaceProfile(any(String.class), any(MarketplaceProfileDto.class));
    }
}
