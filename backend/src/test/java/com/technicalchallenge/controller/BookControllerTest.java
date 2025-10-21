package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.BookDTO;
import com.technicalchallenge.mapper.BookMapper;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.CostCenter;
import com.technicalchallenge.service.BookService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookMapper bookMapper;

    private ObjectMapper objectMapper;
    private BookDTO bookDTO;
    private Book book;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        CostCenter costCenter = new CostCenter();
        costCenter.setCostCenterName("TestCostCenter");

        //Set up for the Book entity
        book = new Book();
        book.setBookName("Book Name");
        book.setId(1L);
        book.setActive(true);
        book.setVersion(1);
        book.setCostCenter(costCenter);

        //Set up for the BookDTO
        bookDTO = new BookDTO();
        bookDTO.setBookName("Book Name");
        bookDTO.setId(1L);
        bookDTO.setActive(true);
        bookDTO.setVersion(1);
        bookDTO.setCostCenterName("TestCostCenter");

        // Set up default mappings
        when(bookService.getAllBooks()).thenReturn(List.of(bookDTO));
        when(bookMapper.toDto(book)).thenReturn(bookDTO);
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);
        doNothing().when(bookService).populateReferenceDataByName(any(Book.class), any(BookDTO.class));

    }

    @Test
    void shouldReturnAllBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }
    // Add more tests for POST, PUT, DELETE as needed

    @Test
    void shouldReturnANewBook() throws Exception {
        //Given
        when(bookService.saveBook(any(BookDTO.class))).thenReturn(bookDTO);

        // When/Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.bookName", is("Book Name")));

        verify(bookService).saveBook(any(BookDTO.class));
    }
}
