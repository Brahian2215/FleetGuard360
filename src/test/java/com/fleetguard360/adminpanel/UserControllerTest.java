package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.Role;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.payload.request.UpdateUserRequest;
import com.fleetguard360.adminpanel.repository.RoleRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import com.fleetguard360.adminpanel.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.Role;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.payload.request.UpdateUserRequest;
import com.fleetguard360.adminpanel.repository.RoleRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import com.fleetguard360.adminpanel.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, Role.ERole.ROLE_ADMIN);
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@demo.com");
        sampleUser.setFullName("Test User");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setRoles(Set.of(role));
        sampleUser.setActive(true);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnAllUsers() throws Exception {
        List<User> userList = List.of(sampleUser);
        given(userRepository.findAll()).willReturn(userList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("test@demo.com"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnUserById() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@demo.com"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@demo.com");
        updateRequest.setPassword("newPass123");
        updateRequest.setActive(true);
        updateRequest.setRoles(Set.of("admin"));

        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
        given(roleRepository.findByName(Role.ERole.ROLE_ADMIN)).willReturn(Optional.of(new Role(1L, Role.ERole.ROLE_ADMIN)));
        given(passwordEncoder.encode("newPass123")).willReturn("encodedNewPass");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@demo.com");

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldDeleteUser() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
        doNothing().when(userRepository).delete(sampleUser);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, Role.ERole.ROLE_ADMIN);
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@demo.com");
        sampleUser.setFullName("Test User");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setRoles(Set.of(role));
        sampleUser.setActive(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        List<User> userList = List.of(sampleUser);
        given(userRepository.findAll()).willReturn(userList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("test@demo.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserById() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@demo.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@demo.com");
        updateRequest.setPassword("newPass123");
        updateRequest.setActive(true);
        updateRequest.setRoles(Set.of("admin"));

        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
        given(roleRepository.findByName(Role.ERole.ROLE_ADMIN)).willReturn(Optional.of(new Role(1L, Role.ERole.ROLE_ADMIN)));
        given(passwordEncoder.encode("newPass123")).willReturn("encodedNewPass");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@demo.com");

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUser() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
        doNothing().when(userRepository).delete(sampleUser);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
