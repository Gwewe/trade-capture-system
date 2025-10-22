package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.CounterpartyDTO;
import com.technicalchallenge.mapper.CounterpartyMapper;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.service.CounterpartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CounterpartyController.class)
public class CounterpartyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CounterpartyService counterpartyService;

    @MockBean
    private CounterpartyMapper counterpartyMapper;

    private ObjectMapper objectMapper;
    private CounterpartyDTO counterpartyDTO;
    private Counterparty counterparty;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        //Setup for the Counterparty Entity
        counterparty = new Counterparty();
        counterparty.setId(1L);
        counterparty.setName("Counterparty 1");
        counterparty.setAddress("Address 1");

        //Setup for the CounterpartyDTO
        counterpartyDTO = new CounterpartyDTO();
        counterpartyDTO.setId(counterparty.getId());
        counterpartyDTO.setName(counterparty.getName());
        counterpartyDTO.setAddress(counterparty.getAddress());
        when(counterpartyService.getAllCounterparties()).thenReturn(List.of(counterparty));
        when(counterpartyMapper.toDto(counterparty)).thenReturn(counterpartyDTO);
        when(counterpartyMapper.toEntity(counterpartyDTO)).thenReturn(counterparty);
    }

    @Test
    void shouldReturnAllCounterparties() throws Exception {
        mockMvc.perform(get("/api/counterparties"))
                .andExpect(status().isOk());
    }
    // Add more tests for POST, PUT, DELETE as needed

    //Test for Post
    @Test
    void shouldReturnANewCounterparty() throws Exception {
        //Given
        when(counterpartyService.saveCounterparty(any(Counterparty.class))).thenReturn(counterparty);

        // When/Then
        mockMvc.perform(post("/api/counterparties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(counterparty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Counterparty 1")));

        verify(counterpartyService).saveCounterparty(any(Counterparty.class));
    }


    //Test for Delete
    @Test
    void shouldDeleteACounterpartyById() throws Exception {
        //Given
        doNothing().when(counterpartyService).deleteCounterparty(1L);

        // When/Then
        mockMvc.perform(delete("/api/counterparties/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());

        verify(counterpartyService).deleteCounterparty(1L);
    }

}

