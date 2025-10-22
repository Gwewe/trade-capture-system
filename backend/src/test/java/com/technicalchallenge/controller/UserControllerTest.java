package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.UserDTO;
import com.technicalchallenge.mapper.ApplicationUserMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.service.ApplicationUserService;
import com.technicalchallenge.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationUserService applicationUserService;
    @MockBean
    private ApplicationUserMapper applicationUserMapper;
    @MockBean
    private UserProfileService userProfileService;

    private ObjectMapper objectMapper;
    private ApplicationUser applicationUser;
    private UserDTO userDTO;
    private UserProfile userProfile;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        //Setup for ApplicationUser entity
        applicationUser = new ApplicationUser();
        applicationUser.setId(2L);
        applicationUser.setActive(true);
        applicationUser.setVersion(1);
        applicationUser.setFirstName("John");
        applicationUser.setLastName("Doe");

        //Setup for UserProfile entity
        userProfile = new UserProfile();
        userProfile.setId(2L);
        userProfile.setUserType("TRADER");
        applicationUser.setUserProfile(userProfile);

        //Setup for UserDTO
        userDTO = new UserDTO();

        userDTO.setId(2L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setActive(true);
        userDTO.setVersion(1);
        userDTO.setUserProfile("TRADER");

        when(userProfileService.getAllUserProfiles()).thenReturn(List.of(userProfile));
        when(applicationUserService.getAllUsers()).thenReturn(List.of(applicationUser));
        when(applicationUserMapper.toDto(any())).thenReturn(userDTO);
        when(applicationUserMapper.toEntity(any())).thenReturn(applicationUser);

    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
    // Add more tests for POST, PUT, DELETE as needed

    //Test for Post
    @Test
    void shouldReturnANewUser() throws Exception {
        //Given
        when(applicationUserService.saveUser(any(ApplicationUser.class))).thenReturn(applicationUser);
        when(applicationUserMapper.toDto(any())).thenReturn(userDTO);
        when(applicationUserMapper.toEntity(any())).thenReturn(applicationUser);

        //When/Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.id", is(2)))
                .andExpect((ResultMatcher) jsonPath("$.firstName", is("John")));

        verify(applicationUserService).saveUser(any(ApplicationUser.class));
    }

    //Test for Delete
    @Test
    void shouldDeleteAUserById() throws Exception {
        //Given
        doNothing().when(applicationUserService).deleteUser(2L);

        // When/Then
        mockMvc.perform(delete("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(applicationUserService).deleteUser(2L);
    }
}
