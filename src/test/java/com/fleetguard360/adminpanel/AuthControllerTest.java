package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.Role;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.payload.request.LoginRequest;
import com.fleetguard360.adminpanel.payload.request.SignupRequest;
import com.fleetguard360.adminpanel.payload.response.JwtResponse;
import com.fleetguard360.adminpanel.repository.RoleRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import com.fleetguard360.adminpanel.security.jwt.JwtUtils;
import com.fleetguard360.adminpanel.security.service.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    Authentication authentication;

    @InjectMocks
    AuthController authController;

    @Test
    void testAuthenticateUserSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "user@mail.com", "pass", List.of());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
    }

    @Test
    void testRegisterUserSuccessWithAdminRole() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("adminUser");
        signupRequest.setEmail("admin@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFullName("Admin");
        signupRequest.setRoles(Set.of("admin"));

        when(userRepository.existsByUsername("adminUser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encoded-password");

        Role role = new Role();
        role.setName(Role.ERole.ROLE_ADMIN);
        when(roleRepository.findByName(Role.ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existingUser");
        signupRequest.setEmail("user@example.com");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testRegisterUserWithMissingRole() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newUser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFullName("New User");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encoded-password");

        when(roleRepository.findByName(Role.ERole.ROLE_VIEWER)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(signupRequest);
        });

        assertEquals("Error: Role is not found.", thrown.getMessage());
    }
}
