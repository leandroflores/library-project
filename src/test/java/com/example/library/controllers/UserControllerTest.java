package com.example.library.controllers;

import com.example.library.TestConfig;
import com.example.library.domain.User;
import com.example.library.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class UserControllerTest {

    private MockMvc mvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = TestConfig.users();
        User user = users.get(0);

        when(service.getAllUsers()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/users/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phone").value(user.getPhone()));
    }

    @Test
    public void testGetUser() throws Exception {
        User user = TestConfig.user();

        when(service.getUserById(user.getId())).thenReturn(Optional.of(user));

        mvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value(user.getPhone()))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value(user.getPhone()));
    }

    @Test
    public void testGetNotFoundUser() throws Exception {
        User user = TestConfig.user();

        when(service.getUserById(user.getId())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        User user = TestConfig.user();
        String userJson = TestConfig.userJson();

        when(service.checkUserParameters(user)).thenReturn(true);
        when(service.createUser(user)).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.post("/users/")
           .contentType(MediaType.APPLICATION_JSON)
           .content(userJson))
           .andExpect(MockMvcResultMatchers.status().isCreated())
           .andExpect(MockMvcResultMatchers.content().json(userJson));
    }

    @Test
    public void testCreateUserWithRepeatedEmail() throws Exception {
        User user = TestConfig.user();

        String userJson = TestConfig.userJson();
        when(service.checkUserParameters(user)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/users/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andExpect(MockMvcResultMatchers.content().string("User email already exists"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = TestConfig.user();

        when(service.deleteUser(user.getId())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.delete("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted successfully"));
    }

    @Test
    public void testDeleteNotFoundUser() throws Exception {
        User user = TestConfig.user();

        when(service.deleteUser(user.getId())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.delete("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = TestConfig.user();
             user.setName("Greg");
        String userJson = new ObjectMapper().writeValueAsString(user);

        when(service.updateUser(user.getId(), user)).thenReturn(Optional.of(user));

        mvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value(user.getPhone()))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value(user.getPhone()));
    }

    @Test
    public void testUpdateNotFoundUser() throws Exception {
        User user = TestConfig.user();
        user.setName("Greg");
        String userJson = new ObjectMapper().writeValueAsString(user);

        when(service.updateUser(user.getId(), user)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));;
    }

}
